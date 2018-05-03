/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.store.parquet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.apache.drill.common.expression.ErrorCollector;
import org.apache.drill.common.expression.ErrorCollectorImpl;
import org.apache.drill.common.expression.ExpressionStringBuilder;
import org.apache.drill.common.expression.LogicalExpression;
import org.apache.drill.common.expression.SchemaPath;
import org.apache.drill.common.expression.ValueExpressions;
import org.apache.drill.common.types.TypeProtos;
import org.apache.drill.exec.compile.sig.ConstantExpressionIdentifier;
import org.apache.drill.exec.expr.ExpressionTreeMaterializer;
import org.apache.drill.exec.expr.fn.FunctionImplementationRegistry;
import org.apache.drill.exec.expr.stat.ParquetFilterPredicate;
import org.apache.drill.exec.ops.UdfUtilities;
import org.apache.drill.exec.physical.EndpointAffinity;
import org.apache.drill.exec.physical.base.AbstractFileGroupScan;
import org.apache.drill.exec.physical.base.GroupScan;
import org.apache.drill.exec.physical.base.ScanStats;
import org.apache.drill.exec.physical.base.ScanStats.GroupScanProperty;
import org.apache.drill.exec.planner.physical.PlannerSettings;
import org.apache.drill.exec.proto.CoordinationProtos;
import org.apache.drill.exec.server.options.OptionManager;
import org.apache.drill.exec.store.ColumnExplorer;
import org.apache.drill.exec.store.dfs.FileSelection;
import org.apache.drill.exec.store.dfs.ReadEntryWithPath;
import org.apache.drill.exec.store.parquet.stat.ColumnStatistics;
import org.apache.drill.exec.store.parquet.stat.ParquetMetaStatCollector;
import org.apache.drill.exec.store.schedule.AffinityCreator;
import org.apache.drill.exec.store.schedule.AssignmentCreator;
import org.apache.drill.exec.store.schedule.EndpointByteMap;
import org.apache.drill.exec.store.schedule.EndpointByteMapImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


import static org.apache.drill.exec.store.parquet.metadata.MetadataBase.ParquetFileMetadata;
import static org.apache.drill.exec.store.parquet.metadata.MetadataBase.RowGroupMetadata;
import static org.apache.drill.exec.store.parquet.metadata.MetadataBase.ParquetTableMetadataBase;

public abstract class AbstractParquetGroupScan extends AbstractFileGroupScan {

  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AbstractParquetGroupScan.class);

  protected List<SchemaPath> columns;
  protected List<ReadEntryWithPath> entries;
  protected LogicalExpression filter;

  protected ParquetTableMetadataBase parquetTableMetadata;
  protected List<RowGroupInfo> rowGroupInfos;
  protected ListMultimap<Integer, RowGroupInfo> mappings;
  protected Set<String> fileSet;

  private List<EndpointAffinity> endpointAffinities;
  private ParquetGroupScanStatistics parquetGroupScanStatistics;

  protected AbstractParquetGroupScan(String userName, List<SchemaPath> columns, List<ReadEntryWithPath> entries, LogicalExpression filter) {
    super(userName);
    this.columns = columns;
    this.entries = entries;
    this.filter = filter;
  }

  // immutable copy constructor
  protected AbstractParquetGroupScan(AbstractParquetGroupScan that) {
    super(that);
    this.columns = that.columns == null ? null : new ArrayList<>(that.columns);
    this.parquetTableMetadata = that.parquetTableMetadata;
    this.rowGroupInfos = that.rowGroupInfos == null ? null : new ArrayList<>(that.rowGroupInfos);
    this.filter = that.filter;
    this.endpointAffinities = that.endpointAffinities == null ? null : new ArrayList<>(that.endpointAffinities);
    this.mappings = that.mappings == null ? null : ArrayListMultimap.create(that.mappings);
    this.parquetGroupScanStatistics = that.parquetGroupScanStatistics == null ? null : new ParquetGroupScanStatistics(that.parquetGroupScanStatistics);
    this.fileSet = that.fileSet == null ? null : new HashSet<>(that.fileSet);
    this.entries = that.entries == null ? null : new ArrayList<>(that.entries);
  }

  @JsonProperty
  public List<SchemaPath> getColumns() {
    return columns;
  }

  @JsonProperty
  public List<ReadEntryWithPath> getEntries() {
    return entries;
  }

  @JsonIgnore
  @Override
  public Collection<String> getFiles() {
    return fileSet;
  }

  @Override
  public boolean hasFiles() {
    return true;
  }

  @Override
  public boolean canPushdownProjects(List<SchemaPath> columns) {
    return true;
  }

  /**
   * Return column value count for the specified column.
   * If does not contain such column, return 0.
   * Is used when applying convert to direct scan rule.
   *
   * @param column column schema path
   * @return column value count
   */
  @Override
  public long getColumnValueCount(SchemaPath column) {
    return parquetGroupScanStatistics.getColumnValueCount(column);
  }

  /**
   * Calculates the affinity each endpoint has for this scan,
   * by adding up the affinity each endpoint has for each rowGroup.
   *
   * @return a list of EndpointAffinity objects
   */
  @Override
  public List<EndpointAffinity> getOperatorAffinity() {
    return endpointAffinities;
  }

  @Override
  public void applyAssignments(List<CoordinationProtos.DrillbitEndpoint> incomingEndpoints) {
    this.mappings = AssignmentCreator.getMappings(incomingEndpoints, rowGroupInfos);
  }

  @Override
  public int getMaxParallelizationWidth() {
    return rowGroupInfos.size();
  }

  @Override
  public String getDigest() {
    return toString();
  }

  @Override
  public ScanStats getScanStats() {
    int columnCount = columns == null ? 20 : columns.size();
    long rowCount = parquetGroupScanStatistics.getRowCount();
    ScanStats scanStats = new ScanStats(GroupScanProperty.EXACT_ROW_COUNT, rowCount, 1, rowCount * columnCount);
    logger.trace("Drill parquet scan statistics: {}", scanStats);
    return scanStats;
  }

  protected List<RowGroupReadEntry> getReadEntries(int minorFragmentId) {
    assert minorFragmentId < mappings.size() : String
        .format("Mappings length [%d] should be longer than minor fragment id [%d] but it isn't.",
            mappings.size(), minorFragmentId);

    List<RowGroupInfo> rowGroupsForMinor = mappings.get(minorFragmentId);

    Preconditions.checkArgument(!rowGroupsForMinor.isEmpty(),
        String.format("MinorFragmentId %d has no read entries assigned", minorFragmentId));

    List<RowGroupReadEntry> entries = new ArrayList<>();
    for (RowGroupInfo rgi : rowGroupsForMinor) {
      RowGroupReadEntry entry = new RowGroupReadEntry(rgi.getPath(), rgi.getStart(), rgi.getLength(), rgi.getRowGroupIndex(), rgi.getNumRecordsToRead());
      entries.add(entry);
    }
    return entries;
  }

  // filter push down methods block start
  @JsonProperty
  @Override
  public LogicalExpression getFilter() {
    return filter;
  }

  public void setFilter(LogicalExpression filter) {
    this.filter = filter;
  }

  @Override
  public GroupScan applyFilter(LogicalExpression filterExpr, UdfUtilities udfUtilities,
                               FunctionImplementationRegistry functionImplementationRegistry, OptionManager optionManager) {

    if (rowGroupInfos.size() == 1 ||
        ! (parquetTableMetadata.isRowGroupPrunable()) ||
        rowGroupInfos.size() > optionManager.getOption(PlannerSettings.PARQUET_ROWGROUP_FILTER_PUSHDOWN_PLANNING_THRESHOLD)
        ) {
      // Stop pruning for 3 cases:
      //    -  1 single parquet file,
      //    -  metadata does not have proper format to support row group level filter pruning,
      //    -  # of row groups is beyond PARQUET_ROWGROUP_FILTER_PUSHDOWN_PLANNING_THRESHOLD.
      return null;
    }

    final Set<SchemaPath> schemaPathsInExpr = filterExpr.accept(new ParquetRGFilterEvaluator.FieldReferenceFinder(), null);

    final List<RowGroupInfo> qualifiedRGs = new ArrayList<>(rowGroupInfos.size());
    Set<String> qualifiedFilePath = new HashSet<>(); // HashSet keeps a fileName unique.

    ParquetFilterPredicate filterPredicate = null;

    for (RowGroupInfo rowGroup : rowGroupInfos) {
      final ColumnExplorer columnExplorer = new ColumnExplorer(optionManager, columns);
      List<String> partitionValues = getPartitionValues(rowGroup);
      Map<String, String> implicitColValues = columnExplorer.populateImplicitColumns(rowGroup.getPath(), partitionValues, supportsFileImplicitColumns());

      ParquetMetaStatCollector statCollector = new ParquetMetaStatCollector(
          parquetTableMetadata,
          rowGroup.getColumns(),
          implicitColValues);

      Map<SchemaPath, ColumnStatistics> columnStatisticsMap = statCollector.collectColStat(schemaPathsInExpr);

      if (filterPredicate == null) {
        ErrorCollector errorCollector = new ErrorCollectorImpl();
        LogicalExpression materializedFilter = ExpressionTreeMaterializer.materializeFilterExpr(
            filterExpr, columnStatisticsMap, errorCollector, functionImplementationRegistry);

        if (errorCollector.hasErrors()) {
          logger.error("{} error(s) encountered when materialize filter expression : {}",
              errorCollector.getErrorCount(), errorCollector.toErrorString());
          return null;
        }
        logger.debug("materializedFilter : {}", ExpressionStringBuilder.toString(materializedFilter));

        Set<LogicalExpression> constantBoundaries = ConstantExpressionIdentifier.getConstantExpressionSet(materializedFilter);
        filterPredicate = (ParquetFilterPredicate) ParquetFilterBuilder.buildParquetFilterPredicate(
            materializedFilter, constantBoundaries, udfUtilities);

        if (filterPredicate == null) {
          return null;
        }
      }

      if (ParquetRGFilterEvaluator.canDrop(filterPredicate, columnStatisticsMap, rowGroup.getRowCount())) {
        continue;
      }

      qualifiedRGs.add(rowGroup);
      qualifiedFilePath.add(rowGroup.getPath());  // TODO : optimize when 1 file contains m row groups.
    }

    if (qualifiedRGs.size() == rowGroupInfos.size() ) {
      // There is no reduction of rowGroups. Return the original groupScan.
      logger.debug("applyFilter does not have any pruning!");
      return null;
    } else if (qualifiedFilePath.size() == 0) {
      logger.debug("All rowgroups have been filtered out. Add back one to get schema from scannner");
      RowGroupInfo rg = rowGroupInfos.iterator().next();
      qualifiedFilePath.add(rg.getPath());
      qualifiedRGs.add(rg);
    }

    logger.debug("applyFilter {} reduce parquet rowgroup # from {} to {}",
      ExpressionStringBuilder.toString(filterExpr), rowGroupInfos.size(), qualifiedRGs.size());

    try {
      AbstractParquetGroupScan cloneGroupScan = cloneWithFileSelection(qualifiedFilePath);
      cloneGroupScan.rowGroupInfos = qualifiedRGs;
      cloneGroupScan.parquetGroupScanStatistics.collect(cloneGroupScan.rowGroupInfos, cloneGroupScan.parquetTableMetadata);
      return cloneGroupScan;

    } catch (IOException e) {
      logger.warn("Could not apply filter prune due to Exception : {}", e);
      return null;
    }
  }
  // filter push down methods block end

  // limit push down methods start
  @Override
  public boolean supportsLimitPushdown() {
    return true;
  }

  @Override
  public GroupScan applyLimit(int maxRecords) {
    maxRecords = Math.max(maxRecords, 1); // Make sure it request at least 1 row -> 1 rowGroup.
    // further optimization : minimize # of files chosen, or the affinity of files chosen.

    // Calculate number of rowGroups to read based on maxRecords and update
    // number of records to read for each of those rowGroups.
    int index = updateRowGroupInfo(maxRecords);

    Set<String> filePaths = rowGroupInfos.subList(0, index).stream()
        .map(ReadEntryWithPath::getPath)
        .collect(Collectors.toSet()); // HashSet keeps a filePath unique.

    // If there is no change in fileSet, no need to create new groupScan.
    if (filePaths.size() == fileSet.size() ) {
      // There is no reduction of rowGroups. Return the original groupScan.
      logger.debug("applyLimit() does not apply!");
      return null;
    }

    logger.debug("applyLimit() reduce parquet file # from {} to {}", fileSet.size(), filePaths.size());

    try {
      AbstractParquetGroupScan newScan = cloneWithFileSelection(filePaths);
      newScan.updateRowGroupInfo(maxRecords);
      return newScan;
    } catch (IOException e) {
      logger.warn("Could not apply rowcount based prune due to Exception : {}", e);
      return null;
    }
  }
  // limit push down methods end

  // partition pruning methods start
  @Override
  public List<SchemaPath> getPartitionColumns() {
    return parquetGroupScanStatistics.getPartitionColumns();
  }

  @JsonIgnore
  public TypeProtos.MajorType getTypeForColumn(SchemaPath schemaPath) {
    return parquetGroupScanStatistics.getTypeForColumn(schemaPath);
  }

  @JsonIgnore
  public <T> T getPartitionValue(String path, SchemaPath column, Class<T> clazz) {
    return clazz.cast(parquetGroupScanStatistics.getPartitionValue(path, column));
  }

  @JsonIgnore
  public Set<String> getFileSet() {
    return fileSet;
  }
  // partition pruning methods end

  // helper method used for partition pruning and filter push down
  @Override
  public void modifyFileSelection(FileSelection selection) {
    List<String> files = selection.getFiles();
    fileSet = new HashSet<>(files);
    entries = new ArrayList<>(files.size());

    entries.addAll(files.stream()
        .map(ReadEntryWithPath::new)
        .collect(Collectors.toList()));

    rowGroupInfos = rowGroupInfos.stream()
        .filter(rowGroupInfo -> fileSet.contains(rowGroupInfo.getPath()))
        .collect(Collectors.toList());
  }


  // protected methods block
  protected void init() throws IOException {
    initInternal();

    assert parquetTableMetadata != null;

    if (fileSet == null) {
      fileSet = new HashSet<>();
      fileSet.addAll(parquetTableMetadata.getFiles().stream()
          .map((Function<ParquetFileMetadata, String>) ParquetFileMetadata::getPath)
          .collect(Collectors.toSet()));
    }

    Map<String, CoordinationProtos.DrillbitEndpoint> hostEndpointMap = new HashMap<>();

    for (CoordinationProtos.DrillbitEndpoint endpoint : getDrillbits()) {
      hostEndpointMap.put(endpoint.getAddress(), endpoint);
    }

    rowGroupInfos = new ArrayList<>();
    for (ParquetFileMetadata file : parquetTableMetadata.getFiles()) {
      int rgIndex = 0;
      for (RowGroupMetadata rg : file.getRowGroups()) {
        RowGroupInfo rowGroupInfo =
            new RowGroupInfo(file.getPath(), rg.getStart(), rg.getLength(), rgIndex, rg.getRowCount());
        EndpointByteMap endpointByteMap = new EndpointByteMapImpl();
        rg.getHostAffinity().keySet().stream()
            .filter(hostEndpointMap::containsKey)
            .forEach(host ->
                endpointByteMap.add(hostEndpointMap.get(host), (long) (rg.getHostAffinity().get(host) * rg.getLength())));

        rowGroupInfo.setEndpointByteMap(endpointByteMap);
        rowGroupInfo.setColumns(rg.getColumns());
        rgIndex++;
        rowGroupInfos.add(rowGroupInfo);
      }
    }

    this.endpointAffinities = AffinityCreator.getAffinityMap(rowGroupInfos);
    this.parquetGroupScanStatistics = new ParquetGroupScanStatistics(rowGroupInfos, parquetTableMetadata);
  }

  protected String getFilterString() {
    return filter == null || filter.equals(ValueExpressions.BooleanExpression.TRUE) ?
        "" : ExpressionStringBuilder.toString(this.filter);
  }

  // abstract methods block start
  protected abstract void initInternal() throws IOException;
  protected abstract Collection<CoordinationProtos.DrillbitEndpoint> getDrillbits();
  protected abstract AbstractParquetGroupScan cloneWithFileSelection(Collection<String> filePaths) throws IOException;
  protected abstract boolean supportsFileImplicitColumns();
  protected abstract List<String> getPartitionValues(RowGroupInfo rowGroupInfo);
  // abstract methods block end

  // private methods block start
  /**
   * Based on maxRecords to read for the scan,
   * figure out how many rowGroups to read
   * and update number of records to read for each of them.
   *
   * @param maxRecords max records to read
   * @return total number of rowGroups to read
   */
  private int updateRowGroupInfo(int maxRecords) {
    long count = 0;
    int index = 0;
    for (RowGroupInfo rowGroupInfo : rowGroupInfos) {
      long rowCount = rowGroupInfo.getRowCount();
      if (count + rowCount <= maxRecords) {
        count += rowCount;
        rowGroupInfo.setNumRecordsToRead(rowCount);
        index++;
        continue;
      } else if (count < maxRecords) {
        rowGroupInfo.setNumRecordsToRead(maxRecords - count);
        index++;
      }
      break;
    }
    return index;
  }
  // private methods block end

}

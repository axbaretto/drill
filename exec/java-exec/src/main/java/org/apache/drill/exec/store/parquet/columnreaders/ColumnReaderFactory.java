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
package org.apache.drill.exec.store.parquet.columnreaders;

import org.apache.drill.common.exceptions.ExecutionSetupException;
import org.apache.drill.exec.ExecConstants;
import org.apache.drill.exec.vector.VarDecimalVector;
import org.apache.drill.exec.vector.NullableVarDecimalVector;
import org.apache.drill.exec.vector.BigIntVector;
import org.apache.drill.exec.vector.BitVector;
import org.apache.drill.exec.vector.DateVector;
import org.apache.drill.exec.vector.Float4Vector;
import org.apache.drill.exec.vector.Float8Vector;
import org.apache.drill.exec.vector.IntVector;
import org.apache.drill.exec.vector.IntervalVector;
import org.apache.drill.exec.vector.NullableBigIntVector;
import org.apache.drill.exec.vector.NullableBitVector;
import org.apache.drill.exec.vector.NullableDateVector;
import org.apache.drill.exec.vector.NullableFloat4Vector;
import org.apache.drill.exec.vector.NullableFloat8Vector;
import org.apache.drill.exec.vector.NullableIntVector;
import org.apache.drill.exec.vector.NullableIntervalVector;
import org.apache.drill.exec.vector.NullableTimeStampVector;
import org.apache.drill.exec.vector.NullableTimeVector;
import org.apache.drill.exec.vector.NullableVarBinaryVector;
import org.apache.drill.exec.vector.NullableVarCharVector;
import org.apache.drill.exec.vector.TimeStampVector;
import org.apache.drill.exec.vector.TimeVector;
import org.apache.drill.exec.vector.UInt4Vector;
import org.apache.drill.exec.vector.UInt8Vector;
import org.apache.drill.exec.vector.ValueVector;
import org.apache.drill.exec.vector.VarBinaryVector;
import org.apache.drill.exec.vector.VarCharVector;
import org.apache.drill.exec.vector.VariableWidthVector;
import org.apache.parquet.column.ColumnDescriptor;
import org.apache.parquet.column.Encoding;
import org.apache.parquet.format.ConvertedType;
import org.apache.parquet.format.SchemaElement;
import org.apache.parquet.hadoop.metadata.ColumnChunkMetaData;
import org.apache.parquet.schema.PrimitiveType;

public class ColumnReaderFactory {

  /**
   * @param fixedLength
   * @param descriptor
   * @param columnChunkMetaData
   * @param allocateSize - the size of the vector to create
   * @return
   * @throws SchemaChangeException
   */
  static ColumnReader<?> createFixedColumnReader(ParquetRecordReader recordReader, boolean fixedLength, ColumnDescriptor descriptor,
                                               ColumnChunkMetaData columnChunkMetaData, int allocateSize, ValueVector v,
                                               SchemaElement schemaElement)
      throws Exception {
    ConvertedType convertedType = schemaElement.getConverted_type();
    // if the column is required, or repeated (in which case we just want to use this to generate our appropriate
    // ColumnReader for actually transferring data into the data vector inside of our repeated vector
    if (descriptor.getMaxDefinitionLevel() == 0 || descriptor.getMaxRepetitionLevel() > 0) {
      if (columnChunkMetaData.getType() == PrimitiveType.PrimitiveTypeName.BOOLEAN){
        return new BitReader(recordReader, allocateSize, descriptor, columnChunkMetaData,
            fixedLength, (BitVector) v, schemaElement);
      } else if (!columnChunkMetaData.getEncodings().contains(Encoding.PLAIN_DICTIONARY) && (
          columnChunkMetaData.getType() == PrimitiveType.PrimitiveTypeName.FIXED_LEN_BYTE_ARRAY
              || columnChunkMetaData.getType() == PrimitiveType.PrimitiveTypeName.INT96)) {
        switch (convertedType) {
          case DECIMAL:
            return new FixedByteAlignedReader.VarDecimalReader(recordReader, allocateSize, descriptor,
                columnChunkMetaData, fixedLength, (VarDecimalVector) v, schemaElement);
          case INTERVAL:
            return new FixedByteAlignedReader.IntervalReader(recordReader, allocateSize, descriptor,
                columnChunkMetaData, fixedLength, (IntervalVector) v, schemaElement);
          default:
            return new FixedByteAlignedReader.FixedBinaryReader(recordReader, allocateSize, descriptor,
                columnChunkMetaData, (VariableWidthVector) v, schemaElement);
        }
      } else if (columnChunkMetaData.getType() == PrimitiveType.PrimitiveTypeName.INT32 && convertedType == ConvertedType.DATE){
        switch(recordReader.getDateCorruptionStatus()) {
          case META_SHOWS_CORRUPTION:
            return new FixedByteAlignedReader.CorruptDateReader(recordReader, allocateSize, descriptor, columnChunkMetaData, fixedLength, (DateVector) v, schemaElement);
          case META_SHOWS_NO_CORRUPTION:
            return new FixedByteAlignedReader.DateReader(recordReader, allocateSize, descriptor, columnChunkMetaData, fixedLength, (DateVector) v, schemaElement);
          case META_UNCLEAR_TEST_VALUES:
            return new FixedByteAlignedReader.CorruptionDetectingDateReader(recordReader, allocateSize, descriptor, columnChunkMetaData, fixedLength, (DateVector) v, schemaElement);
          default:
            throw new ExecutionSetupException(
                String.format("Issue setting up parquet reader for date type, " +
                        "unrecognized date corruption status %s. See DRILL-4203 for more info.",
                recordReader.getDateCorruptionStatus()));
        }
      } else {
        if (columnChunkMetaData.getEncodings().contains(Encoding.PLAIN_DICTIONARY)) {
          switch (columnChunkMetaData.getType()) {
            case INT32:
              if (convertedType == null) {
                return new ParquetFixedWidthDictionaryReaders.DictionaryIntReader(recordReader, allocateSize, descriptor, columnChunkMetaData, fixedLength, (IntVector) v, schemaElement);
              }
              switch (convertedType) {
                case DECIMAL:
                  return new ParquetFixedWidthDictionaryReaders.DictionaryVarDecimalReader(recordReader, allocateSize,
                      descriptor, columnChunkMetaData, fixedLength, (VarDecimalVector) v, schemaElement);
                case TIME_MILLIS:
                  return new ParquetFixedWidthDictionaryReaders.DictionaryTimeReader(recordReader, allocateSize, descriptor, columnChunkMetaData, fixedLength, (TimeVector) v, schemaElement);
                case INT_8:
                case INT_16:
                case INT_32:
                  return new ParquetFixedWidthDictionaryReaders.DictionaryIntReader(recordReader, allocateSize, descriptor, columnChunkMetaData, fixedLength, (IntVector) v, schemaElement);
                case UINT_8:
                case UINT_16:
                case UINT_32:
                  return new ParquetFixedWidthDictionaryReaders.DictionaryUInt4Reader(recordReader, allocateSize, descriptor, columnChunkMetaData, fixedLength, (UInt4Vector) v, schemaElement);
                default:
                  throw new ExecutionSetupException("Unsupported dictionary converted type " + convertedType + " for primitive type INT32");
              }
            case INT64:
              if (convertedType == null) {
                return new ParquetFixedWidthDictionaryReaders.DictionaryBigIntReader(recordReader, allocateSize, descriptor, columnChunkMetaData, fixedLength, (BigIntVector) v, schemaElement);
              }
              switch (convertedType) {
                case INT_64:
                  return new ParquetFixedWidthDictionaryReaders.DictionaryBigIntReader(recordReader, allocateSize, descriptor, columnChunkMetaData, fixedLength, (BigIntVector) v, schemaElement);
                case UINT_64:
                  return new ParquetFixedWidthDictionaryReaders.DictionaryUInt8Reader(recordReader, allocateSize, descriptor, columnChunkMetaData, fixedLength, (UInt8Vector) v, schemaElement);
                case DECIMAL:
                  return new ParquetFixedWidthDictionaryReaders.DictionaryVarDecimalReader(recordReader, allocateSize,
                      descriptor, columnChunkMetaData, fixedLength, (VarDecimalVector) v, schemaElement);
                case TIMESTAMP_MILLIS:
                  return new ParquetFixedWidthDictionaryReaders.DictionaryTimeStampReader(recordReader, allocateSize, descriptor, columnChunkMetaData, fixedLength, (TimeStampVector) v, schemaElement);
                default:
                  throw new ExecutionSetupException("Unsupported dictionary converted type " + convertedType + " for primitive type INT64");
              }
            case FLOAT:
              return new ParquetFixedWidthDictionaryReaders.DictionaryFloat4Reader(recordReader, allocateSize, descriptor, columnChunkMetaData, fixedLength, (Float4Vector) v, schemaElement);
            case DOUBLE:
              return new ParquetFixedWidthDictionaryReaders.DictionaryFloat8Reader(recordReader, allocateSize, descriptor, columnChunkMetaData, fixedLength, (Float8Vector) v, schemaElement);
            case FIXED_LEN_BYTE_ARRAY:
              return new ParquetFixedWidthDictionaryReaders.DictionaryFixedBinaryReader(recordReader, allocateSize, descriptor, columnChunkMetaData, fixedLength, (VarBinaryVector) v, schemaElement);
            case INT96:
              if (recordReader.getFragmentContext().getOptions().getOption(ExecConstants.PARQUET_READER_INT96_AS_TIMESTAMP).bool_val) {
                return new ParquetFixedWidthDictionaryReaders.DictionaryBinaryAsTimeStampReader(recordReader, allocateSize, descriptor, columnChunkMetaData, fixedLength, (TimeStampVector) v, schemaElement);
              } else {
                return new ParquetFixedWidthDictionaryReaders.DictionaryFixedBinaryReader(recordReader, allocateSize, descriptor, columnChunkMetaData, fixedLength, (VarBinaryVector) v, schemaElement);
              }
            default:
              throw new ExecutionSetupException("Unsupported dictionary column type " + descriptor.getType().name() );
          }

        } else if (convertedType == ConvertedType.DECIMAL) {
          return new FixedByteAlignedReader.VarDecimalReader(recordReader, allocateSize,
            descriptor, columnChunkMetaData, fixedLength, (VarDecimalVector) v, schemaElement);
        } else {
          return new FixedByteAlignedReader<>(recordReader, allocateSize, descriptor, columnChunkMetaData,
              fixedLength, v, schemaElement);
        }
      }
    } else { // if the column is nullable
      if (columnChunkMetaData.getType() == PrimitiveType.PrimitiveTypeName.BOOLEAN) {
        return new NullableBitReader(recordReader, allocateSize, descriptor, columnChunkMetaData,
            fixedLength, (NullableBitVector) v, schemaElement);
      } else if (columnChunkMetaData.getType() == PrimitiveType.PrimitiveTypeName.INT32 && convertedType == ConvertedType.DATE) {
        switch(recordReader.getDateCorruptionStatus()) {
          case META_SHOWS_CORRUPTION:
            return new NullableFixedByteAlignedReaders.NullableCorruptDateReader(recordReader, allocateSize, descriptor, columnChunkMetaData, fixedLength, (NullableDateVector)v, schemaElement);
          case META_SHOWS_NO_CORRUPTION:
            return new NullableFixedByteAlignedReaders.NullableDateReader(recordReader, allocateSize, descriptor, columnChunkMetaData, fixedLength, (NullableDateVector) v, schemaElement);
          case META_UNCLEAR_TEST_VALUES:
            return new NullableFixedByteAlignedReaders.CorruptionDetectingNullableDateReader(recordReader, allocateSize, descriptor, columnChunkMetaData, fixedLength, (NullableDateVector) v, schemaElement);
          default:
            throw new ExecutionSetupException(
                String.format("Issue setting up parquet reader for date type, " +
                        "unrecognized date corruption status %s. See DRILL-4203 for more info.",
                    recordReader.getDateCorruptionStatus()));
        }
      } else if (columnChunkMetaData.getType() == PrimitiveType.PrimitiveTypeName.FIXED_LEN_BYTE_ARRAY) {
        if (convertedType == ConvertedType.DECIMAL) {
          return new NullableFixedByteAlignedReaders.NullableVarDecimalReader(recordReader, allocateSize,
              descriptor, columnChunkMetaData, fixedLength, (NullableVarDecimalVector) v, schemaElement);
        } else if (convertedType == ConvertedType.INTERVAL) {
          return new NullableFixedByteAlignedReaders.NullableIntervalReader(recordReader, allocateSize, descriptor,
              columnChunkMetaData, fixedLength, (NullableIntervalVector) v, schemaElement);
        }
      } else {
        return getNullableColumnReader(recordReader, allocateSize, descriptor,
            columnChunkMetaData, fixedLength, v, schemaElement);
      }
    }
    throw new Exception("Unexpected parquet metadata configuration.");
  }

  static VarLengthValuesColumn<?> getReader(ParquetRecordReader parentReader, int allocateSize, ColumnDescriptor descriptor,
                                          ColumnChunkMetaData columnChunkMetaData, boolean fixedLength, ValueVector v,
                                          SchemaElement schemaElement
  ) throws ExecutionSetupException {
    ConvertedType convertedType = schemaElement.getConverted_type();
    switch (descriptor.getMaxDefinitionLevel()) {
      case 0:
        if (convertedType == null) {
          return new VarLengthColumnReaders.VarBinaryColumn(parentReader, allocateSize, descriptor, columnChunkMetaData, fixedLength, (VarBinaryVector) v, schemaElement);
        }
        switch (convertedType) {
          case UTF8:
          case ENUM:
            return new VarLengthColumnReaders.VarCharColumn(parentReader, allocateSize, descriptor, columnChunkMetaData, fixedLength, (VarCharVector) v, schemaElement);
          case DECIMAL:
            if (v instanceof VarDecimalVector) {
              return new VarLengthColumnReaders.VarDecimalColumn(parentReader, allocateSize, descriptor, columnChunkMetaData, fixedLength, (VarDecimalVector) v, schemaElement);
            }
          default:
            return new VarLengthColumnReaders.VarBinaryColumn(parentReader, allocateSize, descriptor, columnChunkMetaData, fixedLength, (VarBinaryVector) v, schemaElement);
        }
      default:
        if (convertedType == null) {
          return new VarLengthColumnReaders.NullableVarBinaryColumn(parentReader, allocateSize, descriptor, columnChunkMetaData, fixedLength, (NullableVarBinaryVector) v, schemaElement);
        }

        switch (convertedType) {
          case UTF8:
          case ENUM:
            return new VarLengthColumnReaders.NullableVarCharColumn(parentReader, allocateSize, descriptor, columnChunkMetaData, fixedLength, (NullableVarCharVector) v, schemaElement);
          case DECIMAL:
            if (v instanceof NullableVarDecimalVector) {
              return new VarLengthColumnReaders.NullableVarDecimalColumn(parentReader, allocateSize, descriptor, columnChunkMetaData, fixedLength, (NullableVarDecimalVector) v, schemaElement);
            }
          default:
            return new VarLengthColumnReaders.NullableVarBinaryColumn(parentReader, allocateSize, descriptor, columnChunkMetaData, fixedLength, (NullableVarBinaryVector) v, schemaElement);
        }
    }
  }

  public static NullableColumnReader<?> getNullableColumnReader(ParquetRecordReader parentReader, int allocateSize,
                                                             ColumnDescriptor columnDescriptor,
                                                             ColumnChunkMetaData columnChunkMetaData,
                                                             boolean fixedLength,
                                                             ValueVector valueVec,
                                                             SchemaElement schemaElement) throws ExecutionSetupException {
    ConvertedType convertedType = schemaElement.getConverted_type();

    if (! columnChunkMetaData.getEncodings().contains(Encoding.PLAIN_DICTIONARY)) {
      if (columnDescriptor.getType() == PrimitiveType.PrimitiveTypeName.INT96) {
         // TODO: check convertedType once parquet support TIMESTAMP_NANOS type annotation.
        if (parentReader.getFragmentContext().getOptions().getOption(ExecConstants.PARQUET_READER_INT96_AS_TIMESTAMP).bool_val) {
          return new NullableFixedByteAlignedReaders.NullableFixedBinaryAsTimeStampReader(parentReader, allocateSize, columnDescriptor, columnChunkMetaData, true, (NullableTimeStampVector) valueVec, schemaElement);
        } else {
          return new NullableFixedByteAlignedReaders.NullableFixedBinaryReader(parentReader, allocateSize, columnDescriptor, columnChunkMetaData, true, (NullableVarBinaryVector) valueVec, schemaElement);
        }
      } else if (convertedType == ConvertedType.DECIMAL) {
        // NullableVarDecimalVector allows storing of values with different width,
        // so every time when the value is added, offset vector should be updated.
        // Therefore NullableVarDecimalReader is used here instead of NullableFixedByteAlignedReader.
        return new NullableFixedByteAlignedReaders.NullableVarDecimalReader(parentReader, allocateSize,
            columnDescriptor, columnChunkMetaData, fixedLength, (NullableVarDecimalVector) valueVec, schemaElement);
      } else {
        return new NullableFixedByteAlignedReaders.NullableFixedByteAlignedReader<>(parentReader, allocateSize, columnDescriptor, columnChunkMetaData, fixedLength, valueVec, schemaElement);
      }
    } else {
      switch (columnDescriptor.getType()) {
        case INT32:
          if (convertedType == null) {
            return new NullableFixedByteAlignedReaders.NullableDictionaryIntReader(parentReader, allocateSize, columnDescriptor, columnChunkMetaData, fixedLength, (NullableIntVector) valueVec, schemaElement);
          }
          switch (convertedType) {
            case DECIMAL:
              return new NullableFixedByteAlignedReaders.NullableDictionaryVarDecimalReader(parentReader, allocateSize,
                  columnDescriptor, columnChunkMetaData, fixedLength, (NullableVarDecimalVector) valueVec, schemaElement);
            case TIME_MILLIS:
              return new NullableFixedByteAlignedReaders.NullableDictionaryTimeReader(parentReader, allocateSize, columnDescriptor, columnChunkMetaData, fixedLength, (NullableTimeVector)valueVec, schemaElement);
            default:
              throw new ExecutionSetupException("Unsupported nullable converted type " + convertedType + " for primitive type INT32");
          }
        case INT64:
          if (convertedType == null) {
            return new NullableFixedByteAlignedReaders.NullableDictionaryBigIntReader(parentReader, allocateSize, columnDescriptor, columnChunkMetaData, fixedLength, (NullableBigIntVector)valueVec, schemaElement);
          }
          switch (convertedType) {
            case DECIMAL:
              return new NullableFixedByteAlignedReaders.NullableDictionaryVarDecimalReader(parentReader, allocateSize,
                  columnDescriptor, columnChunkMetaData, fixedLength, (NullableVarDecimalVector) valueVec, schemaElement);
            case TIMESTAMP_MILLIS:
              return new NullableFixedByteAlignedReaders.NullableDictionaryTimeStampReader(parentReader, allocateSize, columnDescriptor, columnChunkMetaData, fixedLength, (NullableTimeStampVector)valueVec, schemaElement);
            default:
              throw new ExecutionSetupException("Unsupported nullable converted type " + convertedType + " for primitive type INT64");
          }
        case INT96:
          // TODO: check convertedType once parquet support TIMESTAMP_NANOS type annotation.
          if (parentReader.getFragmentContext().getOptions().getOption(ExecConstants.PARQUET_READER_INT96_AS_TIMESTAMP).bool_val) {
            return new NullableFixedByteAlignedReaders.NullableFixedBinaryAsTimeStampReader(parentReader, allocateSize, columnDescriptor, columnChunkMetaData, true, (NullableTimeStampVector) valueVec, schemaElement);
          } else {
            return new NullableFixedByteAlignedReaders.NullableFixedBinaryReader(parentReader, allocateSize, columnDescriptor, columnChunkMetaData, true, (NullableVarBinaryVector) valueVec, schemaElement);
          }
        case FLOAT:
          return new NullableFixedByteAlignedReaders.NullableDictionaryFloat4Reader(parentReader, allocateSize, columnDescriptor, columnChunkMetaData, fixedLength, (NullableFloat4Vector)valueVec, schemaElement);
        case DOUBLE:
          return new NullableFixedByteAlignedReaders.NullableDictionaryFloat8Reader(parentReader, allocateSize, columnDescriptor, columnChunkMetaData, fixedLength, (NullableFloat8Vector)valueVec, schemaElement);
        default:
          throw new ExecutionSetupException("Unsupported nullable column type " + columnDescriptor.getType().name() );
      }
    }
  }
}

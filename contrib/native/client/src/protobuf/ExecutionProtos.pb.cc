// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ExecutionProtos.proto

#define INTERNAL_SUPPRESS_PROTOBUF_FIELD_DEPRECATION
#include "ExecutionProtos.pb.h"

#include <algorithm>

#include <google/protobuf/stubs/common.h>
#include <google/protobuf/stubs/once.h>
#include <google/protobuf/io/coded_stream.h>
#include <google/protobuf/wire_format_lite_inl.h>
#include <google/protobuf/descriptor.h>
#include <google/protobuf/generated_message_reflection.h>
#include <google/protobuf/reflection_ops.h>
#include <google/protobuf/wire_format.h>
// @@protoc_insertion_point(includes)

namespace exec {
namespace bit {

namespace {

const ::google::protobuf::Descriptor* FragmentHandle_descriptor_ = NULL;
const ::google::protobuf::internal::GeneratedMessageReflection*
  FragmentHandle_reflection_ = NULL;
const ::google::protobuf::Descriptor* ServerPreparedStatementState_descriptor_ = NULL;
const ::google::protobuf::internal::GeneratedMessageReflection*
  ServerPreparedStatementState_reflection_ = NULL;

}  // namespace


void protobuf_AssignDesc_ExecutionProtos_2eproto() {
  protobuf_AddDesc_ExecutionProtos_2eproto();
  const ::google::protobuf::FileDescriptor* file =
    ::google::protobuf::DescriptorPool::generated_pool()->FindFileByName(
      "ExecutionProtos.proto");
  GOOGLE_CHECK(file != NULL);
  FragmentHandle_descriptor_ = file->message_type(0);
  static const int FragmentHandle_offsets_[4] = {
    GOOGLE_PROTOBUF_GENERATED_MESSAGE_FIELD_OFFSET(FragmentHandle, query_id_),
    GOOGLE_PROTOBUF_GENERATED_MESSAGE_FIELD_OFFSET(FragmentHandle, major_fragment_id_),
    GOOGLE_PROTOBUF_GENERATED_MESSAGE_FIELD_OFFSET(FragmentHandle, minor_fragment_id_),
    GOOGLE_PROTOBUF_GENERATED_MESSAGE_FIELD_OFFSET(FragmentHandle, parent_query_id_),
  };
  FragmentHandle_reflection_ =
    new ::google::protobuf::internal::GeneratedMessageReflection(
      FragmentHandle_descriptor_,
      FragmentHandle::default_instance_,
      FragmentHandle_offsets_,
      GOOGLE_PROTOBUF_GENERATED_MESSAGE_FIELD_OFFSET(FragmentHandle, _has_bits_[0]),
      GOOGLE_PROTOBUF_GENERATED_MESSAGE_FIELD_OFFSET(FragmentHandle, _unknown_fields_),
      -1,
      ::google::protobuf::DescriptorPool::generated_pool(),
      ::google::protobuf::MessageFactory::generated_factory(),
      sizeof(FragmentHandle));
  ServerPreparedStatementState_descriptor_ = file->message_type(1);
  static const int ServerPreparedStatementState_offsets_[1] = {
    GOOGLE_PROTOBUF_GENERATED_MESSAGE_FIELD_OFFSET(ServerPreparedStatementState, sql_query_),
  };
  ServerPreparedStatementState_reflection_ =
    new ::google::protobuf::internal::GeneratedMessageReflection(
      ServerPreparedStatementState_descriptor_,
      ServerPreparedStatementState::default_instance_,
      ServerPreparedStatementState_offsets_,
      GOOGLE_PROTOBUF_GENERATED_MESSAGE_FIELD_OFFSET(ServerPreparedStatementState, _has_bits_[0]),
      GOOGLE_PROTOBUF_GENERATED_MESSAGE_FIELD_OFFSET(ServerPreparedStatementState, _unknown_fields_),
      -1,
      ::google::protobuf::DescriptorPool::generated_pool(),
      ::google::protobuf::MessageFactory::generated_factory(),
      sizeof(ServerPreparedStatementState));
}

namespace {

GOOGLE_PROTOBUF_DECLARE_ONCE(protobuf_AssignDescriptors_once_);
inline void protobuf_AssignDescriptorsOnce() {
  ::google::protobuf::GoogleOnceInit(&protobuf_AssignDescriptors_once_,
                 &protobuf_AssignDesc_ExecutionProtos_2eproto);
}

void protobuf_RegisterTypes(const ::std::string&) {
  protobuf_AssignDescriptorsOnce();
  ::google::protobuf::MessageFactory::InternalRegisterGeneratedMessage(
    FragmentHandle_descriptor_, &FragmentHandle::default_instance());
  ::google::protobuf::MessageFactory::InternalRegisterGeneratedMessage(
    ServerPreparedStatementState_descriptor_, &ServerPreparedStatementState::default_instance());
}

}  // namespace

void protobuf_ShutdownFile_ExecutionProtos_2eproto() {
  delete FragmentHandle::default_instance_;
  delete FragmentHandle_reflection_;
  delete ServerPreparedStatementState::default_instance_;
  delete ServerPreparedStatementState_reflection_;
}

void protobuf_AddDesc_ExecutionProtos_2eproto() {
  static bool already_here = false;
  if (already_here) return;
  already_here = true;
  GOOGLE_PROTOBUF_VERIFY_VERSION;

  ::exec::protobuf_AddDesc_Coordination_2eproto();
  ::exec::shared::protobuf_AddDesc_UserBitShared_2eproto();
  ::google::protobuf::DescriptorPool::InternalAddGeneratedFile(
    "\n\025ExecutionProtos.proto\022\010exec.bit\032\022Coord"
    "ination.proto\032\023UserBitShared.proto\"\235\001\n\016F"
    "ragmentHandle\022&\n\010query_id\030\001 \001(\0132\024.exec.s"
    "hared.QueryId\022\031\n\021major_fragment_id\030\002 \001(\005"
    "\022\031\n\021minor_fragment_id\030\003 \001(\005\022-\n\017parent_qu"
    "ery_id\030\004 \001(\0132\024.exec.shared.QueryId\"1\n\034Se"
    "rverPreparedStatementState\022\021\n\tsql_query\030"
    "\001 \001(\tB+\n\033org.apache.drill.exec.protoB\nEx"
    "ecProtosH\001", 330);
  ::google::protobuf::MessageFactory::InternalRegisterGeneratedFile(
    "ExecutionProtos.proto", &protobuf_RegisterTypes);
  FragmentHandle::default_instance_ = new FragmentHandle();
  ServerPreparedStatementState::default_instance_ = new ServerPreparedStatementState();
  FragmentHandle::default_instance_->InitAsDefaultInstance();
  ServerPreparedStatementState::default_instance_->InitAsDefaultInstance();
  ::google::protobuf::internal::OnShutdown(&protobuf_ShutdownFile_ExecutionProtos_2eproto);
}

// Force AddDescriptors() to be called at static initialization time.
struct StaticDescriptorInitializer_ExecutionProtos_2eproto {
  StaticDescriptorInitializer_ExecutionProtos_2eproto() {
    protobuf_AddDesc_ExecutionProtos_2eproto();
  }
} static_descriptor_initializer_ExecutionProtos_2eproto_;

// ===================================================================

#ifndef _MSC_VER
const int FragmentHandle::kQueryIdFieldNumber;
const int FragmentHandle::kMajorFragmentIdFieldNumber;
const int FragmentHandle::kMinorFragmentIdFieldNumber;
const int FragmentHandle::kParentQueryIdFieldNumber;
#endif  // !_MSC_VER

FragmentHandle::FragmentHandle()
  : ::google::protobuf::Message() {
  SharedCtor();
}

void FragmentHandle::InitAsDefaultInstance() {
  query_id_ = const_cast< ::exec::shared::QueryId*>(&::exec::shared::QueryId::default_instance());
  parent_query_id_ = const_cast< ::exec::shared::QueryId*>(&::exec::shared::QueryId::default_instance());
}

FragmentHandle::FragmentHandle(const FragmentHandle& from)
  : ::google::protobuf::Message() {
  SharedCtor();
  MergeFrom(from);
}

void FragmentHandle::SharedCtor() {
  _cached_size_ = 0;
  query_id_ = NULL;
  major_fragment_id_ = 0;
  minor_fragment_id_ = 0;
  parent_query_id_ = NULL;
  ::memset(_has_bits_, 0, sizeof(_has_bits_));
}

FragmentHandle::~FragmentHandle() {
  SharedDtor();
}

void FragmentHandle::SharedDtor() {
  if (this != default_instance_) {
    delete query_id_;
    delete parent_query_id_;
  }
}

void FragmentHandle::SetCachedSize(int size) const {
  GOOGLE_SAFE_CONCURRENT_WRITES_BEGIN();
  _cached_size_ = size;
  GOOGLE_SAFE_CONCURRENT_WRITES_END();
}
const ::google::protobuf::Descriptor* FragmentHandle::descriptor() {
  protobuf_AssignDescriptorsOnce();
  return FragmentHandle_descriptor_;
}

const FragmentHandle& FragmentHandle::default_instance() {
  if (default_instance_ == NULL) protobuf_AddDesc_ExecutionProtos_2eproto();
  return *default_instance_;
}

FragmentHandle* FragmentHandle::default_instance_ = NULL;

FragmentHandle* FragmentHandle::New() const {
  return new FragmentHandle;
}

void FragmentHandle::Clear() {
  if (_has_bits_[0 / 32] & (0xffu << (0 % 32))) {
    if (has_query_id()) {
      if (query_id_ != NULL) query_id_->::exec::shared::QueryId::Clear();
    }
    major_fragment_id_ = 0;
    minor_fragment_id_ = 0;
    if (has_parent_query_id()) {
      if (parent_query_id_ != NULL) parent_query_id_->::exec::shared::QueryId::Clear();
    }
  }
  ::memset(_has_bits_, 0, sizeof(_has_bits_));
  mutable_unknown_fields()->Clear();
}

bool FragmentHandle::MergePartialFromCodedStream(
    ::google::protobuf::io::CodedInputStream* input) {
#define DO_(EXPRESSION) if (!(EXPRESSION)) return false
  ::google::protobuf::uint32 tag;
  while ((tag = input->ReadTag()) != 0) {
    switch (::google::protobuf::internal::WireFormatLite::GetTagFieldNumber(tag)) {
      // optional .exec.shared.QueryId query_id = 1;
      case 1: {
        if (::google::protobuf::internal::WireFormatLite::GetTagWireType(tag) ==
            ::google::protobuf::internal::WireFormatLite::WIRETYPE_LENGTH_DELIMITED) {
          DO_(::google::protobuf::internal::WireFormatLite::ReadMessageNoVirtual(
               input, mutable_query_id()));
        } else {
          goto handle_uninterpreted;
        }
        if (input->ExpectTag(16)) goto parse_major_fragment_id;
        break;
      }

      // optional int32 major_fragment_id = 2;
      case 2: {
        if (::google::protobuf::internal::WireFormatLite::GetTagWireType(tag) ==
            ::google::protobuf::internal::WireFormatLite::WIRETYPE_VARINT) {
         parse_major_fragment_id:
          DO_((::google::protobuf::internal::WireFormatLite::ReadPrimitive<
                   ::google::protobuf::int32, ::google::protobuf::internal::WireFormatLite::TYPE_INT32>(
                 input, &major_fragment_id_)));
          set_has_major_fragment_id();
        } else {
          goto handle_uninterpreted;
        }
        if (input->ExpectTag(24)) goto parse_minor_fragment_id;
        break;
      }

      // optional int32 minor_fragment_id = 3;
      case 3: {
        if (::google::protobuf::internal::WireFormatLite::GetTagWireType(tag) ==
            ::google::protobuf::internal::WireFormatLite::WIRETYPE_VARINT) {
         parse_minor_fragment_id:
          DO_((::google::protobuf::internal::WireFormatLite::ReadPrimitive<
                   ::google::protobuf::int32, ::google::protobuf::internal::WireFormatLite::TYPE_INT32>(
                 input, &minor_fragment_id_)));
          set_has_minor_fragment_id();
        } else {
          goto handle_uninterpreted;
        }
        if (input->ExpectTag(34)) goto parse_parent_query_id;
        break;
      }

      // optional .exec.shared.QueryId parent_query_id = 4;
      case 4: {
        if (::google::protobuf::internal::WireFormatLite::GetTagWireType(tag) ==
            ::google::protobuf::internal::WireFormatLite::WIRETYPE_LENGTH_DELIMITED) {
         parse_parent_query_id:
          DO_(::google::protobuf::internal::WireFormatLite::ReadMessageNoVirtual(
               input, mutable_parent_query_id()));
        } else {
          goto handle_uninterpreted;
        }
        if (input->ExpectAtEnd()) return true;
        break;
      }

      default: {
      handle_uninterpreted:
        if (::google::protobuf::internal::WireFormatLite::GetTagWireType(tag) ==
            ::google::protobuf::internal::WireFormatLite::WIRETYPE_END_GROUP) {
          return true;
        }
        DO_(::google::protobuf::internal::WireFormat::SkipField(
              input, tag, mutable_unknown_fields()));
        break;
      }
    }
  }
  return true;
#undef DO_
}

void FragmentHandle::SerializeWithCachedSizes(
    ::google::protobuf::io::CodedOutputStream* output) const {
  // optional .exec.shared.QueryId query_id = 1;
  if (has_query_id()) {
    ::google::protobuf::internal::WireFormatLite::WriteMessageMaybeToArray(
      1, this->query_id(), output);
  }

  // optional int32 major_fragment_id = 2;
  if (has_major_fragment_id()) {
    ::google::protobuf::internal::WireFormatLite::WriteInt32(2, this->major_fragment_id(), output);
  }

  // optional int32 minor_fragment_id = 3;
  if (has_minor_fragment_id()) {
    ::google::protobuf::internal::WireFormatLite::WriteInt32(3, this->minor_fragment_id(), output);
  }

  // optional .exec.shared.QueryId parent_query_id = 4;
  if (has_parent_query_id()) {
    ::google::protobuf::internal::WireFormatLite::WriteMessageMaybeToArray(
      4, this->parent_query_id(), output);
  }

  if (!unknown_fields().empty()) {
    ::google::protobuf::internal::WireFormat::SerializeUnknownFields(
        unknown_fields(), output);
  }
}

::google::protobuf::uint8* FragmentHandle::SerializeWithCachedSizesToArray(
    ::google::protobuf::uint8* target) const {
  // optional .exec.shared.QueryId query_id = 1;
  if (has_query_id()) {
    target = ::google::protobuf::internal::WireFormatLite::
      WriteMessageNoVirtualToArray(
        1, this->query_id(), target);
  }

  // optional int32 major_fragment_id = 2;
  if (has_major_fragment_id()) {
    target = ::google::protobuf::internal::WireFormatLite::WriteInt32ToArray(2, this->major_fragment_id(), target);
  }

  // optional int32 minor_fragment_id = 3;
  if (has_minor_fragment_id()) {
    target = ::google::protobuf::internal::WireFormatLite::WriteInt32ToArray(3, this->minor_fragment_id(), target);
  }

  // optional .exec.shared.QueryId parent_query_id = 4;
  if (has_parent_query_id()) {
    target = ::google::protobuf::internal::WireFormatLite::
      WriteMessageNoVirtualToArray(
        4, this->parent_query_id(), target);
  }

  if (!unknown_fields().empty()) {
    target = ::google::protobuf::internal::WireFormat::SerializeUnknownFieldsToArray(
        unknown_fields(), target);
  }
  return target;
}

int FragmentHandle::ByteSize() const {
  int total_size = 0;

  if (_has_bits_[0 / 32] & (0xffu << (0 % 32))) {
    // optional .exec.shared.QueryId query_id = 1;
    if (has_query_id()) {
      total_size += 1 +
        ::google::protobuf::internal::WireFormatLite::MessageSizeNoVirtual(
          this->query_id());
    }

    // optional int32 major_fragment_id = 2;
    if (has_major_fragment_id()) {
      total_size += 1 +
        ::google::protobuf::internal::WireFormatLite::Int32Size(
          this->major_fragment_id());
    }

    // optional int32 minor_fragment_id = 3;
    if (has_minor_fragment_id()) {
      total_size += 1 +
        ::google::protobuf::internal::WireFormatLite::Int32Size(
          this->minor_fragment_id());
    }

    // optional .exec.shared.QueryId parent_query_id = 4;
    if (has_parent_query_id()) {
      total_size += 1 +
        ::google::protobuf::internal::WireFormatLite::MessageSizeNoVirtual(
          this->parent_query_id());
    }

  }
  if (!unknown_fields().empty()) {
    total_size +=
      ::google::protobuf::internal::WireFormat::ComputeUnknownFieldsSize(
        unknown_fields());
  }
  GOOGLE_SAFE_CONCURRENT_WRITES_BEGIN();
  _cached_size_ = total_size;
  GOOGLE_SAFE_CONCURRENT_WRITES_END();
  return total_size;
}

void FragmentHandle::MergeFrom(const ::google::protobuf::Message& from) {
  GOOGLE_CHECK_NE(&from, this);
  const FragmentHandle* source =
    ::google::protobuf::internal::dynamic_cast_if_available<const FragmentHandle*>(
      &from);
  if (source == NULL) {
    ::google::protobuf::internal::ReflectionOps::Merge(from, this);
  } else {
    MergeFrom(*source);
  }
}

void FragmentHandle::MergeFrom(const FragmentHandle& from) {
  GOOGLE_CHECK_NE(&from, this);
  if (from._has_bits_[0 / 32] & (0xffu << (0 % 32))) {
    if (from.has_query_id()) {
      mutable_query_id()->::exec::shared::QueryId::MergeFrom(from.query_id());
    }
    if (from.has_major_fragment_id()) {
      set_major_fragment_id(from.major_fragment_id());
    }
    if (from.has_minor_fragment_id()) {
      set_minor_fragment_id(from.minor_fragment_id());
    }
    if (from.has_parent_query_id()) {
      mutable_parent_query_id()->::exec::shared::QueryId::MergeFrom(from.parent_query_id());
    }
  }
  mutable_unknown_fields()->MergeFrom(from.unknown_fields());
}

void FragmentHandle::CopyFrom(const ::google::protobuf::Message& from) {
  if (&from == this) return;
  Clear();
  MergeFrom(from);
}

void FragmentHandle::CopyFrom(const FragmentHandle& from) {
  if (&from == this) return;
  Clear();
  MergeFrom(from);
}

bool FragmentHandle::IsInitialized() const {

  return true;
}

void FragmentHandle::Swap(FragmentHandle* other) {
  if (other != this) {
    std::swap(query_id_, other->query_id_);
    std::swap(major_fragment_id_, other->major_fragment_id_);
    std::swap(minor_fragment_id_, other->minor_fragment_id_);
    std::swap(parent_query_id_, other->parent_query_id_);
    std::swap(_has_bits_[0], other->_has_bits_[0]);
    _unknown_fields_.Swap(&other->_unknown_fields_);
    std::swap(_cached_size_, other->_cached_size_);
  }
}

::google::protobuf::Metadata FragmentHandle::GetMetadata() const {
  protobuf_AssignDescriptorsOnce();
  ::google::protobuf::Metadata metadata;
  metadata.descriptor = FragmentHandle_descriptor_;
  metadata.reflection = FragmentHandle_reflection_;
  return metadata;
}


// ===================================================================

#ifndef _MSC_VER
const int ServerPreparedStatementState::kSqlQueryFieldNumber;
#endif  // !_MSC_VER

ServerPreparedStatementState::ServerPreparedStatementState()
  : ::google::protobuf::Message() {
  SharedCtor();
}

void ServerPreparedStatementState::InitAsDefaultInstance() {
}

ServerPreparedStatementState::ServerPreparedStatementState(const ServerPreparedStatementState& from)
  : ::google::protobuf::Message() {
  SharedCtor();
  MergeFrom(from);
}

void ServerPreparedStatementState::SharedCtor() {
  _cached_size_ = 0;
  sql_query_ = const_cast< ::std::string*>(&::google::protobuf::internal::kEmptyString);
  ::memset(_has_bits_, 0, sizeof(_has_bits_));
}

ServerPreparedStatementState::~ServerPreparedStatementState() {
  SharedDtor();
}

void ServerPreparedStatementState::SharedDtor() {
  if (sql_query_ != &::google::protobuf::internal::kEmptyString) {
    delete sql_query_;
  }
  if (this != default_instance_) {
  }
}

void ServerPreparedStatementState::SetCachedSize(int size) const {
  GOOGLE_SAFE_CONCURRENT_WRITES_BEGIN();
  _cached_size_ = size;
  GOOGLE_SAFE_CONCURRENT_WRITES_END();
}
const ::google::protobuf::Descriptor* ServerPreparedStatementState::descriptor() {
  protobuf_AssignDescriptorsOnce();
  return ServerPreparedStatementState_descriptor_;
}

const ServerPreparedStatementState& ServerPreparedStatementState::default_instance() {
  if (default_instance_ == NULL) protobuf_AddDesc_ExecutionProtos_2eproto();
  return *default_instance_;
}

ServerPreparedStatementState* ServerPreparedStatementState::default_instance_ = NULL;

ServerPreparedStatementState* ServerPreparedStatementState::New() const {
  return new ServerPreparedStatementState;
}

void ServerPreparedStatementState::Clear() {
  if (_has_bits_[0 / 32] & (0xffu << (0 % 32))) {
    if (has_sql_query()) {
      if (sql_query_ != &::google::protobuf::internal::kEmptyString) {
        sql_query_->clear();
      }
    }
  }
  ::memset(_has_bits_, 0, sizeof(_has_bits_));
  mutable_unknown_fields()->Clear();
}

bool ServerPreparedStatementState::MergePartialFromCodedStream(
    ::google::protobuf::io::CodedInputStream* input) {
#define DO_(EXPRESSION) if (!(EXPRESSION)) return false
  ::google::protobuf::uint32 tag;
  while ((tag = input->ReadTag()) != 0) {
    switch (::google::protobuf::internal::WireFormatLite::GetTagFieldNumber(tag)) {
      // optional string sql_query = 1;
      case 1: {
        if (::google::protobuf::internal::WireFormatLite::GetTagWireType(tag) ==
            ::google::protobuf::internal::WireFormatLite::WIRETYPE_LENGTH_DELIMITED) {
          DO_(::google::protobuf::internal::WireFormatLite::ReadString(
                input, this->mutable_sql_query()));
          ::google::protobuf::internal::WireFormat::VerifyUTF8String(
            this->sql_query().data(), this->sql_query().length(),
            ::google::protobuf::internal::WireFormat::PARSE);
        } else {
          goto handle_uninterpreted;
        }
        if (input->ExpectAtEnd()) return true;
        break;
      }

      default: {
      handle_uninterpreted:
        if (::google::protobuf::internal::WireFormatLite::GetTagWireType(tag) ==
            ::google::protobuf::internal::WireFormatLite::WIRETYPE_END_GROUP) {
          return true;
        }
        DO_(::google::protobuf::internal::WireFormat::SkipField(
              input, tag, mutable_unknown_fields()));
        break;
      }
    }
  }
  return true;
#undef DO_
}

void ServerPreparedStatementState::SerializeWithCachedSizes(
    ::google::protobuf::io::CodedOutputStream* output) const {
  // optional string sql_query = 1;
  if (has_sql_query()) {
    ::google::protobuf::internal::WireFormat::VerifyUTF8String(
      this->sql_query().data(), this->sql_query().length(),
      ::google::protobuf::internal::WireFormat::SERIALIZE);
    ::google::protobuf::internal::WireFormatLite::WriteString(
      1, this->sql_query(), output);
  }

  if (!unknown_fields().empty()) {
    ::google::protobuf::internal::WireFormat::SerializeUnknownFields(
        unknown_fields(), output);
  }
}

::google::protobuf::uint8* ServerPreparedStatementState::SerializeWithCachedSizesToArray(
    ::google::protobuf::uint8* target) const {
  // optional string sql_query = 1;
  if (has_sql_query()) {
    ::google::protobuf::internal::WireFormat::VerifyUTF8String(
      this->sql_query().data(), this->sql_query().length(),
      ::google::protobuf::internal::WireFormat::SERIALIZE);
    target =
      ::google::protobuf::internal::WireFormatLite::WriteStringToArray(
        1, this->sql_query(), target);
  }

  if (!unknown_fields().empty()) {
    target = ::google::protobuf::internal::WireFormat::SerializeUnknownFieldsToArray(
        unknown_fields(), target);
  }
  return target;
}

int ServerPreparedStatementState::ByteSize() const {
  int total_size = 0;

  if (_has_bits_[0 / 32] & (0xffu << (0 % 32))) {
    // optional string sql_query = 1;
    if (has_sql_query()) {
      total_size += 1 +
        ::google::protobuf::internal::WireFormatLite::StringSize(
          this->sql_query());
    }

  }
  if (!unknown_fields().empty()) {
    total_size +=
      ::google::protobuf::internal::WireFormat::ComputeUnknownFieldsSize(
        unknown_fields());
  }
  GOOGLE_SAFE_CONCURRENT_WRITES_BEGIN();
  _cached_size_ = total_size;
  GOOGLE_SAFE_CONCURRENT_WRITES_END();
  return total_size;
}

void ServerPreparedStatementState::MergeFrom(const ::google::protobuf::Message& from) {
  GOOGLE_CHECK_NE(&from, this);
  const ServerPreparedStatementState* source =
    ::google::protobuf::internal::dynamic_cast_if_available<const ServerPreparedStatementState*>(
      &from);
  if (source == NULL) {
    ::google::protobuf::internal::ReflectionOps::Merge(from, this);
  } else {
    MergeFrom(*source);
  }
}

void ServerPreparedStatementState::MergeFrom(const ServerPreparedStatementState& from) {
  GOOGLE_CHECK_NE(&from, this);
  if (from._has_bits_[0 / 32] & (0xffu << (0 % 32))) {
    if (from.has_sql_query()) {
      set_sql_query(from.sql_query());
    }
  }
  mutable_unknown_fields()->MergeFrom(from.unknown_fields());
}

void ServerPreparedStatementState::CopyFrom(const ::google::protobuf::Message& from) {
  if (&from == this) return;
  Clear();
  MergeFrom(from);
}

void ServerPreparedStatementState::CopyFrom(const ServerPreparedStatementState& from) {
  if (&from == this) return;
  Clear();
  MergeFrom(from);
}

bool ServerPreparedStatementState::IsInitialized() const {

  return true;
}

void ServerPreparedStatementState::Swap(ServerPreparedStatementState* other) {
  if (other != this) {
    std::swap(sql_query_, other->sql_query_);
    std::swap(_has_bits_[0], other->_has_bits_[0]);
    _unknown_fields_.Swap(&other->_unknown_fields_);
    std::swap(_cached_size_, other->_cached_size_);
  }
}

::google::protobuf::Metadata ServerPreparedStatementState::GetMetadata() const {
  protobuf_AssignDescriptorsOnce();
  ::google::protobuf::Metadata metadata;
  metadata.descriptor = ServerPreparedStatementState_descriptor_;
  metadata.reflection = ServerPreparedStatementState_reflection_;
  return metadata;
}


// @@protoc_insertion_point(namespace_scope)

}  // namespace bit
}  // namespace exec

// @@protoc_insertion_point(global_scope)

// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Frame.proto

package org.springframework.boot.netty.codec.proto;

public interface FrameOrBuilder extends
    // @@protoc_insertion_point(interface_extends:Frame)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * messageName约定为要发送的message的类全名
   * </pre>
   *
   * <code>string head = 1;</code>
   */
  java.lang.String getHead();
  /**
   * <pre>
   * messageName约定为要发送的message的类全名
   * </pre>
   *
   * <code>string head = 1;</code>
   */
  com.google.protobuf.ByteString
      getHeadBytes();

  /**
   * <pre>
   * 所有消息在发送的时候都序列化成byte数组写入Frame的payload
   * </pre>
   *
   * <code>bytes payload = 2;</code>
   */
  com.google.protobuf.ByteString getPayload();

  /**
   * <pre>
   * payload crc32校验码
   * </pre>
   *
   * <code>string crcCode = 3;</code>
   */
  java.lang.String getCrcCode();
  /**
   * <pre>
   * payload crc32校验码
   * </pre>
   *
   * <code>string crcCode = 3;</code>
   */
  com.google.protobuf.ByteString
      getCrcCodeBytes();
}
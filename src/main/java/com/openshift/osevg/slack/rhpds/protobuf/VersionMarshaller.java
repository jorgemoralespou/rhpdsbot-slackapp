package com.openshift.osevg.slack.rhpds.protobuf;

import java.io.IOException;

import com.openshift.osevg.slack.rhpds.Version;

import org.infinispan.protostream.MessageMarshaller;

public class VersionMarshaller implements MessageMarshaller<Version> {

  @Override
  public Class<? extends Version> getJavaClass() {
    return Version.class;
  }

  @Override
  public String getTypeName() {
    return "slack_rhpds.Version";
  }

  @Override
  public Version readFrom(ProtoStreamReader reader) throws IOException {
    int version = reader.readInt("version");
    return new Version(version);
  }

  @Override
  public void writeTo(ProtoStreamWriter writer, Version version) throws IOException {
    writer.writeInt("version", version.getVersion());
  }

}
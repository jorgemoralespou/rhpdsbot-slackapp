package com.openshift.osevg.slack.rhpds.protobuf;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.openshift.osevg.slack.rhpds.Cluster;
import com.openshift.osevg.slack.rhpds.Version;

import org.infinispan.protostream.MessageMarshaller;

public class ClusterMarshaller implements MessageMarshaller<Cluster> {

  private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
  @Override
  public Class<? extends Cluster> getJavaClass() {
    return Cluster.class;
  }

  @Override
  public String getTypeName() {
    return "slack_rhpds.Cluster";
  }

  @Override
  public Cluster readFrom(ProtoStreamReader reader) throws IOException {
    String name = reader.readString("name");
    Version version = reader.readObject("version", Version.class);
    String owner = reader.readString("owner");
    String createdStr = reader.readString("created");
    String url = reader.readString("url");
    return new Cluster(name, version, owner, LocalDateTime.parse(createdStr, formatter), url);
  }

  @Override
  public void writeTo(ProtoStreamWriter writer, Cluster cluster) throws IOException {
    writer.writeString("name", cluster.getName());
    writer.writeObject("version", cluster.getVersion(), Version.class);
    writer.writeString("owner", cluster.getOwner());
    writer.writeString("created", cluster.getCreated().format(formatter));
    writer.writeString("url", cluster.getUrl());
  }

}
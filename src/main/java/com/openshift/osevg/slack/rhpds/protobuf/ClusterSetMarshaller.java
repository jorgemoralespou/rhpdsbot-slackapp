package com.openshift.osevg.slack.rhpds.protobuf;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.openshift.osevg.slack.rhpds.Cluster;
import com.openshift.osevg.slack.rhpds.ClusterSet;

import org.infinispan.protostream.MessageMarshaller;

public class ClusterSetMarshaller implements MessageMarshaller<ClusterSet> {

  @Override
  public Class<? extends ClusterSet> getJavaClass() {
    return ClusterSet.class;
  }

  @Override
  public String getTypeName() {
    return "slack_rhpds.ClusterSet";
  }

  @Override
  public ClusterSet readFrom(ProtoStreamReader reader) throws IOException {
    Set<Cluster> clusters = reader.readCollection("clusters", new HashSet<>(), Cluster.class);
    return new ClusterSet(clusters);
  }

  @Override
  public void writeTo(ProtoStreamWriter writer, ClusterSet cluster) throws IOException {
    writer.writeCollection("clusters", cluster.getAll(), Cluster.class);
  }

}
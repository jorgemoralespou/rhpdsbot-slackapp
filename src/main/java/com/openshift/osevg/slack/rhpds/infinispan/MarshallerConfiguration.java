package com.openshift.osevg.slack.rhpds.infinispan;

import com.openshift.osevg.slack.rhpds.protobuf.ClusterMarshaller;
import com.openshift.osevg.slack.rhpds.protobuf.ClusterSetMarshaller;
import com.openshift.osevg.slack.rhpds.protobuf.VersionMarshaller;
import org.infinispan.protostream.MessageMarshaller;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class MarshallerConfiguration {

  @Produces
  MessageMarshaller clusterMarshaller() {
    return new ClusterMarshaller();
  }

  @Produces
  MessageMarshaller clusterSetMarshaller() {
    return new ClusterSetMarshaller();
  }

  @Produces
  MessageMarshaller versionMarshaller() {
    return new VersionMarshaller();
  }
}

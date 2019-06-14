package com.openshift.osevg.slack.rhpds.infinispan;

import com.openshift.osevg.slack.rhpds.Cluster;
import com.openshift.osevg.slack.rhpds.ClusterSet;
import com.openshift.osevg.slack.rhpds.Version;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;

import org.infinispan.commons.configuration.ClassWhiteList;
import org.infinispan.commons.dataconversion.MediaType;
import org.infinispan.commons.io.ByteBuffer;
import org.infinispan.commons.io.ByteBufferImpl;
import org.infinispan.commons.marshall.AbstractMarshaller;
import org.infinispan.commons.marshall.CheckedInputStream;

public class ClusterSetMarshaller extends AbstractMarshaller{

  private final ClassWhiteList whiteList;

  public ClusterSetMarshaller() {
     this(new ClassWhiteList(Collections.emptyList()));
  }

  public ClusterSetMarshaller(ClassWhiteList whiteList) {
     this.whiteList = whiteList;
     whiteList.addClasses(Version.class, Cluster.class, ClusterSet.class);
  }

  @Override
  protected ByteBuffer objectToBuffer(Object o, int estimatedSize) throws IOException {
     ByteArrayOutputStream baos = new ByteArrayOutputStream();
     ObjectOutput out = new ObjectOutputStream(baos);
     out.writeObject(o);
     out.close();
     baos.close();
     byte[] bytes = baos.toByteArray();
     return new ByteBufferImpl(bytes, 0, bytes.length);
  }

  @Override
  public Object objectFromByteBuffer(byte[] buf, int offset, int length) throws IOException, ClassNotFoundException {
     try (ObjectInputStream ois = new CheckedInputStream(new ByteArrayInputStream(buf), whiteList)) {
        return ois.readObject();
     }
  }

  @Override
  public boolean isMarshallable(Object o) throws Exception {
     return o instanceof Serializable;
  }

  @Override
  public MediaType mediaType() {
     return MediaType.APPLICATION_SERIALIZED_OBJECT;
  }

}
package com.openshift.osevg.slack.rhpds;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import com.openshift.osevg.slack.rhpds.protobuf.ClusterMarshaller;
import com.openshift.osevg.slack.rhpds.protobuf.ClusterSetMarshaller;
import com.openshift.osevg.slack.rhpds.protobuf.VersionMarshaller;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.protostream.BaseMarshaller;
import org.infinispan.protostream.MessageMarshaller;

import io.quarkus.infinispan.client.runtime.Remote;

@ApplicationScoped
@Named("Infinispan")
public class InfinispanClusterService implements ClusterService {

  @Inject
  RemoteCacheManager remoteCacheManager;

//  @Inject InfinispanClusterService(RemoteCacheManager remoteCacheManager) {
//    this.remoteCacheManager = remoteCacheManager;
//  }

  @Inject @Remote("default")
  RemoteCache<String, ClusterSet> cache;

//  void onStart(@Observes StartupEvent ev) {
    //LOGGER.info("Create or get cache named mycache with the default configuration");
//    cache = remoteCacheManager.administration().getOrCreateCache("mycache", "default");
// }
  @Override
  public ClusterSet list(String clustersKey) {
    if (cache.containsKey(clustersKey)){
      return cache.get(clustersKey);
    }
    return null;
  }

  @Override
  public ClusterSet filter(String clustersKey, String filter) {
    ClusterSet myReturnClusters = new ClusterSet();

    if (cache.containsKey(clustersKey)){
      cache.get(clustersKey).getAll().forEach(cluster -> {
        if (cluster.getName().contains(filter)){
          myReturnClusters.add(cluster);
        }
      });
    }
    return myReturnClusters;
  }

  @Override
  public void add(String clustersKey, String name, String url) {
    add(clustersKey, new Cluster(name, url));
  }

  @Override
  public void add(String clustersKey, Cluster cluster) {
    if (cache.containsKey(clustersKey)){
      cache.get(clustersKey).add(cluster);
    }else{
      ClusterSet myClusters = new ClusterSet();
      myClusters.add(cluster);
      cache.put(clustersKey, myClusters);
    }
  }

  @Override
  public void delete(String clustersKey, String clusterName) {
    delete(clustersKey, new Cluster(clusterName,""));
  }

  @Override
  public void delete(String clustersKey, Cluster cluster) {
    if (cache.containsKey(clustersKey)){
      cache.get(clustersKey).getAll().remove(cluster);
    }
  }

  @Override
  public Cluster get(String clustersKey, String name) {
    if (cache.containsKey(clustersKey)){
      return cache.get(clustersKey).get(name);
    }else{
      return null;
    }
  }

  @Produces
  BaseMarshaller<Cluster> clusterMarshaller() {
     return new ClusterMarshaller();
  }

  @Produces
  BaseMarshaller<ClusterSet> clusterSetMarshaller() {
     return new ClusterSetMarshaller();
  }

  @Produces
  BaseMarshaller<Version> versionMarshaller() {
     return new VersionMarshaller();
  }

}
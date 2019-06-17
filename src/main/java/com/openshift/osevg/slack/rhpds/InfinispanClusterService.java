package com.openshift.osevg.slack.rhpds;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;

import io.quarkus.infinispan.client.runtime.Remote;

@ApplicationScoped
@Named("Infinispan")
public class InfinispanClusterService implements ClusterService {

  @Inject
  RemoteCacheManager remoteCacheManager;

  @Inject @Remote("default")
  RemoteCache<String, ClusterSet> cache;

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
    ClusterSet myClusters;
    if (cache.containsKey(clustersKey)){
      myClusters = cache.get(clustersKey);
    }else {
      myClusters = new ClusterSet();
    }
    myClusters.add(cluster);
    cache.put(clustersKey, myClusters);
  }

  @Override
  public void delete(String clustersKey, String clusterName) {
    delete(clustersKey, new Cluster(clusterName,""));
  }

  @Override
  public void delete(String clustersKey, Cluster cluster) {
    if (cache.containsKey(clustersKey)){
      ClusterSet clusters = cache.get(clustersKey);
      clusters.delete(cluster);
      cache.put(clustersKey, clusters);
    }
  }

  @Override
  public Cluster get(String clustersKey, String name) {
    if (cache.containsKey(clustersKey)) {
      return cache.get(clustersKey).get(name);
    } else {
      return null;
    }
  }
}
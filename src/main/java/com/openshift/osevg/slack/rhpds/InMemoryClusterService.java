package com.openshift.osevg.slack.rhpds;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@ApplicationScoped
@Named("InMemory")
public class InMemoryClusterService implements ClusterService {

    private Map <String, ClusterSet> clusters = new HashMap<String, ClusterSet>();

    public ClusterSet list(String clustersKey){
      if (clusters.containsKey(clustersKey)){
        return clusters.get(clustersKey);
      }
      return null;
    }

    public ClusterSet filter(String clustersKey, String filter){
      ClusterSet myReturnClusters = new ClusterSet();

      if (clusters.containsKey(clustersKey)){
        clusters.get(clustersKey).getAll().forEach(cluster -> {
          if (cluster.getName().contains(filter)){
            myReturnClusters.add(cluster);
          }
        });
      }
      return myReturnClusters;
    }

    public void add(String clustersKey, String name, String url){
      add(clustersKey, new Cluster(name, url));
    }

    public void add(String clustersKey, Cluster cluster){
      if (clusters.containsKey(clustersKey)){
        clusters.get(clustersKey).add(cluster);
      }else{
        ClusterSet myClusters = new ClusterSet();
        myClusters.add(cluster);
        clusters.put(clustersKey, myClusters);
      }
    }

    public void delete(String clustersKey, String clusterName){
        delete(clustersKey, new Cluster(clusterName,""));
    }

    public void delete(String clustersKey, Cluster cluster){
      if (clusters.containsKey(clustersKey)){
        clusters.get(clustersKey).getAll().remove(cluster);
      }
    }

    public Cluster get(String clustersKey, String name){
      if (clusters.containsKey(clustersKey)){
        return clusters.get(clustersKey).get(name);
      }else{
        return null;
      }
    }

}
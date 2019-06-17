package com.openshift.osevg.slack.rhpds;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ClusterSet implements PrettyPrinter, Serializable{

  private static final long serialVersionUID = 289818268456768894L;
  
  private Set<Cluster> clusters;

    public ClusterSet(){
      clusters = new HashSet<Cluster>();
    }

    public ClusterSet(Set<Cluster> set){
      clusters = set;
    }

    public Set<Cluster> getAll(){
      return clusters;
    }

    public Set<Cluster> filter(String filter){
      Set<Cluster> returnSet = new HashSet<Cluster>();
      clusters.forEach(cluster -> {
        if (cluster.getName().contains(filter)){
          returnSet.add(cluster);
        }
      });
      return returnSet;
    }

    public void add(String name, String url){
      add(new Cluster(name, url));
    }

    public void add(Cluster cluster){
      if (cluster.getCreated()==null) 
        cluster.setCreated(LocalDateTime.now());
      clusters.add(cluster);
    }

    public void delete(String clusterName){
      delete(new Cluster(clusterName,""));
    }

    public void delete(Cluster cluster){
      clusters.remove(cluster);
    }

    public Cluster get(String name){
      for(Cluster cluster: clusters){
        if (cluster.getName().equals(name)){
          return cluster;
        }
      }
      return null;
    }

  @Override
  public String toString() {
    return "ClusterList [clusters=" + clusters + "]";
  }

  @Override
  public String toPrettyString() {
    StringBuffer buf = new StringBuffer();

    for(Cluster cluster: clusters){
      buf.append("- ").append(cluster.toPrettyString()).append("\n");
    }
    return buf.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClusterSet that = (ClusterSet) o;
    return Objects.equals(clusters, that.clusters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(clusters);
  }
}
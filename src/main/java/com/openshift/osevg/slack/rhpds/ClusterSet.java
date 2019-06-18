package com.openshift.osevg.slack.rhpds;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@Entity
@NamedQuery(name = "ClusterSet.findByName",
            query = "SELECT c FROM ClusterSet c WHERE c.name=:name")
public class ClusterSet implements PrettyPrinter, Serializable{

    private static final long serialVersionUID = 289818268456768894L;

    private static final String DEFAULT = "default";
  
    @Id
    @Column(name="name")
    private String name;

    @OneToMany
    private Set<Cluster> clusters;

    public ClusterSet(){
      name = DEFAULT;
      clusters = new HashSet<Cluster>();
    }

    public ClusterSet(Set<Cluster> set){
      name = DEFAULT;
      clusters = set;
    }

    public ClusterSet(String name){
      this.name = name;
      clusters = new HashSet<Cluster>();
    }

    public ClusterSet(String name, Set<Cluster> set){
      this.name = name;
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
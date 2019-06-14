package com.openshift.osevg.slack.rhpds;

public interface ClusterService {
  public ClusterSet list(String clustersKey);
  public ClusterSet filter(String clustersKey, String filter);
  public void add(String clustersKey, String name, String url);
  public void add(String clustersKey, Cluster cluster);
  public void delete(String clustersKey, String clusterName);
  public void delete(String clustersKey, Cluster cluster);
  public Cluster get(String clustersKey, String name);
}
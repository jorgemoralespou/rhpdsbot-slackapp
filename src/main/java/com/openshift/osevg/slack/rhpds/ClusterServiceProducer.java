package com.openshift.osevg.slack.rhpds;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class ClusterServiceProducer{

  private static final String IN_MEMORY="memory";
  private static final String CACHE="cache";
  private static final String DATABASE="database";

  @ConfigProperty(name = "clusterservice.impl", defaultValue=ClusterServiceProducer.DATABASE )
  public String serviceImpl;

  @Produces
  public ClusterService getClusterService(){
    if (IN_MEMORY.equals(serviceImpl)){
      return new InMemoryClusterService();
    }else if (CACHE.equals(serviceImpl)){
      return new InfinispanClusterService();
    }else
      return new DatabaseClusterService();
  }

}
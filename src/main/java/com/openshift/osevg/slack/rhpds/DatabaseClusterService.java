package com.openshift.osevg.slack.rhpds;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

@ApplicationScoped
@Named("Database")
public class DatabaseClusterService implements ClusterService {

    @Inject
    EntityManager em;

//    private Map <String, ClusterSet> clusters = new HashMap<String, ClusterSet>();

    public ClusterSet list(String clustersKey){
//      Query q = em.createNamedQuery("ClusterSet.findByName");
      Query q = em.createQuery("SELECT c from ClusterSet c where c.name=:name");
      q.setParameter("name", clustersKey);
      ClusterSet cs = null;
      if (q.getResultList().size()>0){
         cs = (ClusterSet) q.getSingleResult();
      }

      return cs;
    }

    public ClusterSet filter(String clustersKey, String filter){
      ClusterSet myReturnClusters = new ClusterSet();

      Query q = em.createQuery("SELECT c from ClusterSet c where c.name=:name");
      q.setParameter("name", clustersKey);
      ClusterSet cs = (ClusterSet) q.getSingleResult();
      if (cs!=null){
        cs.getAll().forEach(cluster -> {
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

    @Transactional
    public void add(String clustersKey, Cluster cluster){
      Query q = em.createQuery("SELECT c from ClusterSet c where c.name=:channel");
      q.setParameter("channel", clustersKey);
      ClusterSet cs;
      if (q.getResultList().size()==0){
        cs = new ClusterSet(clustersKey);
        cs.add(cluster);
        em.persist(cluster);
        em.persist(cs);
      }else{
        cs = (ClusterSet) q.getSingleResult();
        if (cs.get(cluster.getName())==null){
          cs.add(cluster);
          em.persist(cluster);
        }
      }
    }

    public void delete(String clustersKey, String clusterName){
        delete(clustersKey, new Cluster(clusterName,""));
    }

    @Transactional
    public void delete(String clustersKey, Cluster cluster){
      Query q = em.createQuery("SELECT c from ClusterSet c where c.name=:channel");
      q.setParameter("channel", clustersKey);
      ClusterSet cs;
      if (q.getResultList().size()!=0){
        cs = (ClusterSet) q.getSingleResult();
        cs.delete(cluster);
        em.remove(em.contains(cluster) ? cluster : em.merge(cluster));
        em.persist(cs);
      }
    }

    public Cluster get(String clustersKey, String name){
      Query q = em.createQuery("SELECT c from ClusterSet c where c.name=:channel");
      q.setParameter("channel", clustersKey);
      ClusterSet cs = (ClusterSet) q.getSingleResult();
      if (cs!=null){
        return cs.get(name);
      }
      return null;
    }

}
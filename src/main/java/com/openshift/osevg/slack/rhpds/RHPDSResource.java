package com.openshift.osevg.slack.rhpds;

import java.net.InetAddress;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.openshift.osevg.slack.rhpds.Version;
import com.openshift.osevg.slack.rhpds.slack.SlackAppResponse;
import com.openshift.osevg.slack.rhpds.slack.SlackAppResponseType;
import com.openshift.osevg.slack.rhpds.slack.SlackChallenge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.scheduler.Scheduled;

@Path("/rhpds")
public class RHPDSResource {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private static String helpMessage = "You can 'add' or 'delete' a cluster from the 'list' of available clusters for the OSEVG team to use.\n" +
                                 "Type: '/rhpds_envs help add' to learn how to add a cluster\n" + 
                                 "Type: '/rhpds_envs help delete' to learn how to delete a cluster\n" + 
                                 "Type: '/rhpds_envs help list' to learn how to list all available clusters\n" + 
                                 "Type: '/rhpds_envs help verify' to learn how to verify the status of a cluster\n";

    @Inject @Named("Database")
    ClusterService service;
/*
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/cluster/{name}")
    public Cluster getCluster(@PathParam("name") String name) {
        return service.get(name);
    }

    @DELETE
    @Path("/cluster/{name}")
    public Response deleteCluster(@PathParam("name") String name) {
        service.delete(name);
        return Response.status(204).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/cluster")
    public Map<String, Cluster> listClusters() {
        return service.list();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/cluster")
    public Response addCluster(@Valid Cluster cluster) {
        service.add(cluster);
        return Response.status(201).entity(cluster).build();
    }
*/

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/test")
    public Response listClusters() {
      service.add("sample", new Cluster("sample", "http://example.com"));
      return Response.status(200).entity(new SlackAppResponse("OK")).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/challenge")
    public Response replyChallenge(SlackChallenge challengeRequest) {
        SlackChallenge challengeResponse = new SlackChallenge();
        challengeResponse.setChallenge(challengeRequest.getChallenge());
        return Response.status(200).entity(challengeResponse).build();
    }

    private static final String PATTERN_COMMAND = "^(?:\\s*)(list|add|delete|verify|help)\\b\\s?(.*)";
    private static final String PATTERN_ADD = "^(?:\\s*)<(.*)>";
    private static final String PATTERN_DELETE = "^(?:\\s*)(.*)\\b";
    private static final String PATTERN_VERIFY = "^(?:\\s*)(.*)\\b";
    private static final String PATTERN_HELP = "^(?:\\s*)(list|add|delete|verify)\\b(.*)";

    private static final String PATTERN_OCP3_RHPDS = "(https?://master\\.(.*)\\.open\\.redhat\\.com).*";
    private static final String PATTERN_OCP4_RHPDS = "(https?://console-openshift-console\\.apps\\.(.*)\\.(?:.*)\\.open\\.redhat\\.com).*";

    private static final Pattern pattern_command = Pattern.compile(PATTERN_COMMAND);

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Path("/")
    public Response envs(@FormParam("token") String token,
                         @FormParam("team_id") String teamId,
                         @FormParam("team_domain") String teamDomain,
                         @FormParam("enterprise_id") String enterpriseId,
                         @FormParam("channel_id") String channelId,
                         @FormParam("channel_name") String channelName,
                         @FormParam("user_id") String userId,
                         @FormParam("user_name") String userName,
                         @FormParam("command") String command,
                         @FormParam("text") String text,
                         @FormParam("response_url") String responseUrl,
                         @FormParam("trigger_id") String triggerId) {
        // command
        log.info("Command received: {}", command);
        log.info("Command params(text): {}", text);
        log.info("User: {} [{}]", userName, userId);
        log.info("Channel: {} [{}]", channelName, channelId);
        log.info("Token: {}", token);
        log.info("Response url: {}", responseUrl);
        log.info("TriggerId: {}", triggerId);
        log.info("-------");
        //   /rhpds_envs [add|delete|list|verify]
        // Parse first token in the cluster
        Matcher matcher = pattern_command.matcher(text);
        boolean isMatch = matcher.matches();
        if (isMatch){
          if(matcher.group(1).equals("add")){
            return add(matcher.group(2), userName, channelId);
          } else if(matcher.group(1).equals("delete")){
            return delete(matcher.group(2), userName, channelId);
          } else if(matcher.group(1).equals("list")){
            return list(matcher.group(2), userName, channelId);
          } else if(matcher.group(1).equals("verify")){
            return verify(matcher.group(2), userName, channelId);
          } else return help(matcher.group(2), userName, channelId);
        }else{
          return help("","","");
        }
    }

    private static final Pattern pattern_help = Pattern.compile(PATTERN_HELP);
    
    private Response help(String params, String user, String channel){
      // Provide per command help
      Matcher matcher = pattern_help.matcher(params);
      boolean isMatch = matcher.matches();
      String message = helpMessage;
      if (isMatch){
        if(matcher.group(1).equals("add")){
          message = "/rhpds_envs add <cluster_url>";
        } else if(matcher.group(1).equals("delete")){
          message = "/rhpds_envs delete <cluster-name>";
        } else if(matcher.group(1).equals("list")){
          message = "/rhpds_envs list";
        } else if(matcher.group(1).equals("verify")){
          message = "/rhpds_envs verify <cluster-name>";
        }
      }
      return Response.status(200).entity(new SlackAppResponse(message)).build();
    }

    private static final Pattern pattern_add = Pattern.compile(PATTERN_ADD);
    
    private Response add(String params, String user, String channel){
      System.out.println("Adding [" + params + "] by user [" + user + "] in channel ["+channel+"]");
      
      Matcher matcher = pattern_add.matcher(params);
      boolean isMatch = matcher.matches();
      if (isMatch){
        String givenURL = matcher.group(1);
        Cluster myCluster = new Cluster();
        myCluster.setCreated(LocalDateTime.now());
        Pattern patternOCP4 = Pattern.compile(PATTERN_OCP4_RHPDS);
        Matcher matcherOCP4 = patternOCP4.matcher(givenURL);
        if (matcherOCP4.matches()){
          myCluster.setUrl(matcherOCP4.group(1));
          myCluster.setVersion(Version.V4);
          myCluster.setName(matcherOCP4.group(2));
        }else{
          Pattern patternOCP3 = Pattern.compile(PATTERN_OCP3_RHPDS);
          Matcher matcherOCP3 = patternOCP3.matcher(givenURL);
          if (matcherOCP3.matches()){
            myCluster.setUrl(matcherOCP3.group(1));
            myCluster.setVersion(Version.V3);
            myCluster.setName(matcherOCP3.group(2));
          }
          else{
            return Response.status(200).entity(new SlackAppResponse("Cluster can not be detected as OCP3 or OCP4 based on URL.")).build();
          }
        }
        myCluster.setOwner(user);
        service.add(channel, myCluster);
        return Response.status(200).entity(new SlackAppResponse(myCluster.toPrettyString(), SlackAppResponseType.in_channel)).build();
      }
      return Response.status(200).entity(new SlackAppResponse("/rhpds_envs add <cluster_url>")).build();
    }

    private static final Pattern pattern_delete = Pattern.compile(PATTERN_DELETE);
    private Response delete(String params, String user, String channel){
      Matcher matcher = pattern_delete.matcher(params);
      boolean isMatch = matcher.matches();
      if (isMatch) {
        String clusterName = matcher.group(1);
        service.delete(channel, clusterName);
        return Response.status(200).entity(new SlackAppResponse("Cluster " + clusterName + " deleted", SlackAppResponseType.in_channel)).build();
      }else
        return Response.status(200).entity(new SlackAppResponse(helpMessage)).build();
    }

    private Response list(String params, String user, String channel){
      ClusterSet myList = service.list(channel);
      return Response.status(200).entity(new SlackAppResponse(myList==null? "There's no clusters" : myList.toPrettyString())).build();
    }

    private static final Pattern pattern_verify = Pattern.compile(PATTERN_VERIFY);
      
    private Response verify(String params, String user, String channel){
      Matcher matcher = pattern_verify.matcher(params);
      boolean isMatch = matcher.matches();
      if (isMatch) {
        String clusterName = matcher.group(1);
        Cluster cl = service.get(channel, clusterName);
        if (cl!=null && cl.getUrl()!=null){
          try{
            URI url = new URI(cl.getUrl());
            InetAddress.getByName(url.getHost()).isReachable(2);
          }catch(Exception e){
            service.delete(channel, clusterName);
            String errorMessage = "Cluster " + clusterName + " no longer available. REMOVING IT!"; 
            log.info(errorMessage + " " + e.getMessage());
            return Response.status(200).entity(new SlackAppResponse(errorMessage, SlackAppResponseType.in_channel)).build();
          }
          return Response.status(200).entity(new SlackAppResponse("Cluster is available")).build();
        }
        return Response.status(200).entity(new SlackAppResponse("No cluster available with that name: " + clusterName)).build();
      }else
        return Response.status(200).entity(new SlackAppResponse(helpMessage)).build();
    }

/*
    @Scheduled(every="10s")
    private void scheduledVerify(){
      service.getAll().forEach(cluster -> {
        if (!verify(cluster.getUrl())){
          // TODO: How can I get the clusterKey?
          service.delete("clustersKey", cluster);
        }
      });
    }

    private boolean verify(String clusterUrl){
      try{
        URI url = new URI(clusterUrl);
        InetAddress.getByName(url.getHost()).isReachable(2);
      }catch(Exception e){
        return false;
      }
      return true;
    }
*/
}

package com.openshift.osevg.slack.rhpds;

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
import com.openshift.osevg.slack.rhpds.slack.SlackChallenge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/rhpds")
public class RHPDSResource {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private static String helpMessage = "You can 'add' or 'delete' a cluster from the 'list' of available clusters for the OSEVG team to use.\n" +
                                 "Type: '/rhpds_envs help add' to learn how to add a cluster\n" + 
                                 "Type: '/rhpds_envs help delete' to learn how to delete a cluster\n" + 
                                 "Type: '/rhpds_envs help list' to learn how to list all available clusters\n" + 
                                 "Type: '/rhpds_envs help verify' to learn how to verify the status of a cluster\n";

    @Inject @Named("Infinispan")
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
    private static final String PATTERN_LIST = "^(?:\\s*)(.*)\\b";
    private static final String PATTERN_VERIFY = "^(?:\\s*)(.*)\\b";
    private static final String PATTERN_HELP = "^(?:\\s*)(list|add|delete|verify)\\b(.*)";

    private static final String PATTERN_OCP3_RHPDS = "(https://master\\.(.*)\\.openshiftworkshop\\.com).*";
    private static final String PATTERN_OCP4_RHPDS = "(https://console-openshift-console\\.apps\\.(.*)\\.(?:.*)\\.openshiftworkshop\\.com).*";

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
        Pattern pattern = Pattern.compile(PATTERN_COMMAND);
        Matcher matcher = pattern.matcher(text);
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

    private Response help(String params, String user, String channel){
      // Provide per command help
      Pattern pattern = Pattern.compile(PATTERN_HELP);
      Matcher matcher = pattern.matcher(params);
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

    private Response add(String params, String user, String channel){
      System.out.println("Adding [" + params + "] by user [" + user + "] in channel ["+channel+"]");
      Pattern pattern = Pattern.compile(PATTERN_ADD);
      Matcher matcher = pattern.matcher(params);
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
        return Response.status(200).entity(new SlackAppResponse(myCluster.toPrettyString())).build();
      }
      return Response.status(200).entity(new SlackAppResponse("/rhpds_envs add <cluster_url>")).build();
    }

    private Response delete(String params, String user, String channel){
      Pattern pattern = Pattern.compile(PATTERN_DELETE);
      Matcher matcher = pattern.matcher(params);
      boolean isMatch = matcher.matches();
      if (isMatch) {
        service.delete(channel, matcher.group(1));
        return Response.status(200).entity(new SlackAppResponse("Cluster deleted")).build();
      }else
        return Response.status(200).entity(new SlackAppResponse(helpMessage)).build();
    }

    private Response list(String params, String user, String channel){
      ClusterSet myList = service.list(channel);
      return Response.status(200).entity(new SlackAppResponse(myList==null? "There's no clusters" : myList.toPrettyString())).build();
    }

    private Response verify(String params, String user, String channel){
      return Response.status(200).entity(new SlackAppResponse(helpMessage)).build();
    }

}
package com.openshift.osevg.slack.rhpds.slack;

import java.util.List;

public class EventWrapper {

  public String token;
  public String team_id;
  public String api_app_id;
  public Event event;
  public String type;
  public List<String> authed_users;
  public String event_id;
  public Integer event_time;
}
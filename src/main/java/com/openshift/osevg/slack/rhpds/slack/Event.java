package com.openshift.osevg.slack.rhpds.slack;

public class Event{

  public String type;
  public String event_ts;
  public String user;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getEvent_ts() {
    return event_ts;
  }

  public void setEvent_ts(String event_ts) {
    this.event_ts = event_ts;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  
}
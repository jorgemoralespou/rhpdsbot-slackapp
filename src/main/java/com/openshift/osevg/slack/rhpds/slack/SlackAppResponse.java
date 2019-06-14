package com.openshift.osevg.slack.rhpds.slack;

public class SlackAppResponse {

  private SlackAppResponseType responseType = SlackAppResponseType.ephemeral;
  private String text;

  public SlackAppResponse() {
  }

  public SlackAppResponse(String text) {
    this.text = text;
  }

  public SlackAppResponse(String text, SlackAppResponseType responseType) {
    this.text = text;
    this.responseType = responseType;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public SlackAppResponseType getResponseType() {
    return responseType;
  }

  public void setResponseType(SlackAppResponseType responseType) {
    this.responseType = responseType;
  }

}
package com.openshift.osevg.slack.rhpds;

import java.io.Serializable;

public class Version implements Serializable{

  private static final long serialVersionUID = 1L;

  public static final Version V3 = new Version(3);
  public static final Version V4 = new Version(4);

  private int version;

  public Version(){}

  public Version(int version){
    this.version = version;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

}
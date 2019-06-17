package com.openshift.osevg.slack.rhpds;

import java.io.Serializable;
import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Version version1 = (Version) o;
    return version == version1.version;
  }

  @Override
  public int hashCode() {

    return Objects.hash(version);
  }

  @Override
  public String toString() {
    return "OCP" + version;
  }
}
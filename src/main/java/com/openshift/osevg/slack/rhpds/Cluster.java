package com.openshift.osevg.slack.rhpds;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Cluster implements PrettyPrinter, Serializable{

  private static final long serialVersionUID = 9059576234681297697L;

  private String name;

  private Version version;

  private String owner;

  private LocalDateTime created;

  private String url;

  public Cluster() {
  }

  public Cluster(String name, String url) {
    this.name = name;
    this.version = Version.V3;
    this.owner = "None";
    this.created = LocalDateTime.now();
    this.url = url;
  }

  public Cluster(String name, Version version, String owner, LocalDateTime created, String url) {
    this.name = name;
    this.version = version;
    this.owner = owner;
//    if (created!=null)
//      this.created = created;
//    else
      this.created = LocalDateTime.now();
    this.url = url;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Version getVersion() {
    return version;
  }

  public void setVersion(Version version) {
    this.version = version;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }


  public LocalDateTime getCreated() {
    return created;
  }

  public void setCreated(LocalDateTime created) {
    this.created = created;
  }

  @Override
  public String toString() {
    return "Cluster [created=" + created + ", name=" + name + ", owner=" + owner + ", url=" + url + ", version="
        + version + "]";
  }

  public String toPrettyString() {
    StringBuffer buf = new StringBuffer();

    buf.append("").append("`").append(name).append("`")
        .append(" [")
        .append(version)
        .append("] ")
        .append("at ").append(url)
        .append(" ")
        .append("created on ").append(created)
        .append(" ")
        .append("by ").append(owner)
        .append("");
    return buf.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Cluster cluster = (Cluster) o;
    return Objects.equals(name, cluster.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
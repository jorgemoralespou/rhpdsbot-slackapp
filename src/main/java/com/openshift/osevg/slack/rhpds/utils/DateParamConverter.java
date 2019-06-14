package com.openshift.osevg.slack.rhpds.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.ws.rs.ext.ParamConverter;

public class DateParamConverter implements ParamConverter<LocalDateTime> {

  public static final String DATE_PATTERN = "yyyyMMdd";

  @Override
  public LocalDateTime fromString(String param) {
    if (param == null || param.trim().isEmpty()){
      return null;
    }
    return LocalDateTime.parse(param, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
  }

  @Override
  public String toString(LocalDateTime date) {
     return date==null? "": date.toString();
  }
}
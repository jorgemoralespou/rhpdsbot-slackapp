package com.openshift.osevg.slack.rhpds.utils;

import javax.ws.rs.ext.Provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;

@Provider
public class DateParamConverterProvider implements ParamConverterProvider {

  @SuppressWarnings("unchecked")
  @Override
  public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
     if (rawType.isAssignableFrom(LocalDateTime.class)) {
        return (ParamConverter<T>) new DateParamConverter();
     }
     return null;
  }
}
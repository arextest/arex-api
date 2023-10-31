package com.arextest.web.core.business;

public interface ConfigLoadService {

  String compareIgnoredTimePrecisionMillis = "arex.compare.ignoredTimePrecisionMillis";

  String compareNameToLower = "arex.compare.nameToLower";

  String compareNullEqualsEmpty = "arex.compare.nullEqualsEmpty";

  Object getProperty(String key, Object defaultValue);

  default String getCompareIgnoredTimePrecisionMillis(String defaultValue) {
    return (String) getProperty(compareIgnoredTimePrecisionMillis, defaultValue);
  }

  default String getCompareNameToLower(String defaultValue) {
    return (String) getProperty(compareNameToLower, defaultValue);
  }

  default String getCompareNullEqualsEmpty(String defaultValue) {
    return (String) getProperty(compareNullEqualsEmpty, defaultValue);
  }

}

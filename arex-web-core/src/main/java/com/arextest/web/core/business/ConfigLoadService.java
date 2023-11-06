package com.arextest.web.core.business;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface ConfigLoadService {

  String COMPARE_IGNORED_TIME_PRECISION_MILLIS = "arex.compare.ignoredTimePrecisionMillis";

  String COMPARE_NAME_TO_LOWER = "arex.compare.nameToLower";

  String COMPARE_NULL_EQUALS_EMPTY = "arex.compare.nullEqualsEmpty";

  String COMPARE_IGNORE_NODE_SET = "arex.compare.ignoreNodeSet";

  Object getProperty(String key, Object defaultValue);

  default String getCompareIgnoredTimePrecisionMillis(String defaultValue) {
    return (String) getProperty(COMPARE_IGNORED_TIME_PRECISION_MILLIS, defaultValue);
  }

  default String getCompareNameToLower(String defaultValue) {
    return (String) getProperty(COMPARE_NAME_TO_LOWER, defaultValue);
  }

  default String getCompareNullEqualsEmpty(String defaultValue) {
    return (String) getProperty(COMPARE_NULL_EQUALS_EMPTY, defaultValue);
  }

  default Set<String> getIgnoreNodeSet(String defaultValue) {
    String property = (String) getProperty(COMPARE_IGNORE_NODE_SET, defaultValue);
    if (property == null || property.isEmpty()) {
      return null;
    }
    return new HashSet<>(Arrays.asList(property.split(",")));
  }

}

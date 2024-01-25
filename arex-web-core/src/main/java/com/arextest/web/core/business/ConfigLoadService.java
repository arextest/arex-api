package com.arextest.web.core.business;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface ConfigLoadService {

  String COMPARE_IGNORED_TIME_PRECISION_MILLIS = "arex.compare.ignoredTimePrecisionMillis";

  String COMPARE_NAME_TO_LOWER = "arex.compare.nameToLower";

  String COMPARE_NULL_EQUALS_EMPTY = "arex.compare.nullEqualsEmpty";

  String COMPARE_IGNORE_NODE_SET = "arex.compare.ignoreNodeSet";

  String COMPARE_SELECT_IGNORE_COMPARE = "arex.compare.selectIgnoreCompare";

  String COMPARE_ONLY_COMPARE_COINCIDENT_COLUMN = "arex.compare.onlyCompareCoincidentColumn";

  String COMPARE_UUID_IGNORE = "arex.compare.uuidIgnore";

  String COMPARE_IP_IGNORE = "arex.compare.ipIgnore";

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

  default String getCompareSelectIgnoreCompare(String defaultValue) {
    return (String) getProperty(COMPARE_SELECT_IGNORE_COMPARE, defaultValue);
  }

  default String getCompareOnlyCompareCoincidentColumn(String defaultValue) {
    return (String) getProperty(COMPARE_ONLY_COMPARE_COINCIDENT_COLUMN, defaultValue);
  }

  default String getCompareUuidIgnore(String defaultValue) {
    return (String) getProperty(COMPARE_UUID_IGNORE, defaultValue);
  }

  default String getCompareIpIgnore(String defaultValue) {
    return (String) getProperty(COMPARE_IP_IGNORE, defaultValue);
  }

}

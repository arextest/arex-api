package com.arextest.web.model.enums;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ReplayStatusType {
  public static final Integer INIT = 0;
  public static final Integer RUNNING = 1;
  public static final Integer FINISHED = 2;
  public static final Integer FAIL_INTERRUPTED = 3;
  public static final Integer CANCELLED = 4;
  public static final Integer RERUNNING = 6;

  public static final Set<Integer> NOT_FINISHED_STATUS =
      new HashSet<>(Arrays.asList(INIT, RUNNING, RERUNNING));
}

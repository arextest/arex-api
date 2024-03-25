package com.arextest.web.model.enums;

import java.util.Arrays;
import java.util.List;

public class FSInfoItem {

  public static final int INTERFACE = 1;
  public static final int CASE = 2;
  public static final int FOLDER = 3;


  public static final List<Integer> ALL_TYPES = Arrays.asList(INTERFACE, CASE, FOLDER);
}

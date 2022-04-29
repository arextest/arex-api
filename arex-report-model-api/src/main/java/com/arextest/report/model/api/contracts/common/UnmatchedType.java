package com.arextest.report.model.api.contracts.common;


public class UnmatchedType {

    private UnmatchedType() {
    }

    public static final int NA = 0;
    public static final int LEFT_MISSING = 1;
    public static final int RIGHT_MISSING = 2;
    public static final int UNMATCHED = 3;
    public static final int DIFFERENT_COUNT = 4;
    public static final int NOT_UNIQUE = 5;
    public static final int NOT_SUPPORT = 6;
    public static final int REFERENCE_NOT_FOUND = 7;
    public static final int EXPECT_NOT_NULL = 8;
    public static final int NOT_EXPECT_VALUE = 9;
    public static final int NOT_EXPECT_LIST_COUNT = 10;
    public static final int DIFFERENT_TYPE = 11;
    public static final int OTHERS = 12;
}

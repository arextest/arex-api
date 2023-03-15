package com.arextest.web.model.contract.contracts;

import lombok.Data;

@Data
public class FullLinkInfoItem {
    private String id;
    /**
     * -1 : exception
     * 0: success
     * 1: value diff
     * 2: left call missing
     * 4: right call missing
     */
    private int code;
    private String categoryName;
    private String operationName;

    public static class ItemStatus {
        public final static int EXCEPTION = -1;
        public final static int SUCCESS = 0;
        public final static int VALUE_DIFF = 1;
        public final static int LEFT_CALL_MISSING = 2;
        public final static int RIGHT_CALL_MISSING = 4;
    }

}
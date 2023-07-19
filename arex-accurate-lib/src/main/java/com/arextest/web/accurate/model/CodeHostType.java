package com.arextest.web.accurate.model;

import lombok.Getter;

/**
 * Created by Qzmo on 2023/7/18
 */
public enum CodeHostType {
    GITLAB(0),
    GITHUB(1);

    @Getter
    private int code;
    CodeHostType(int code) {
        this.code = code;
    }

    public static CodeHostType fromCode(int code) {
        switch (code) {
            case 0:
                return GITLAB;
            case 1:
                return GITHUB;
            default:
                return null;
        }
    }
}

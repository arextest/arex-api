package com.arextest.web.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ContractTypeEnum {
    UNKNOWN(-1), GLOBAL(0), ENTRY(1), DEPENDENCY(2);

    @Getter
    private final Integer code;

    public static ContractTypeEnum from(int code) {
        for (ContractTypeEnum type : ContractTypeEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return UNKNOWN;
    }
}

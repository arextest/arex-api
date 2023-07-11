package com.arextest.web.model.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ContractTypeEnum {
    UNKNOWN(0),
    GLOBAL(1),
    ENTRY(2),
    DEPENDENCY(3);

    @Getter
    private final int code;

    public static ContractTypeEnum from(int code) {
        for (ContractTypeEnum type : ContractTypeEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return UNKNOWN;
    }
}

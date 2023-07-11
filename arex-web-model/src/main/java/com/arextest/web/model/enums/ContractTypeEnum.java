package com.arextest.web.model.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ContractTypeEnum {
    GLOBAL(1),
    ENTRY(2),
    DEPENDENCY(3);

    @Getter
    private final int code;
}

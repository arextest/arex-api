package com.arextest.web.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wildeslam.
 * @create 2023/9/25 15:59
 */
@AllArgsConstructor
public enum SystemConfigTypeEnum {
    UNKNOWN(0),
    DESENSITIZATION_JAR(1),
    CALLBACK_INFORM(2),
    ;

    @Getter
    private final Integer code;

}

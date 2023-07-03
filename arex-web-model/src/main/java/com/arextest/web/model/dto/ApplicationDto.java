package com.arextest.web.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ApplicationDto extends BaseDto {
    private String operationId;
    private String operationName;
    private String operationType;
    private String contract;
}

package com.arextest.web.model.dto.filesystem;

import lombok.Data;

@Data
public class FSInterfaceDto extends FSInterfaceAndCaseBaseDto {
    private String operationId;
    private String operationResponse;
}

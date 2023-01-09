package com.arextest.web.model.dto.filesystem;

import lombok.Data;

@Data
public class FSInterfaceDto extends FSInterfaceBaseDto {
    private String recordId;
    private String operationId;
    private String operationResponse;
}

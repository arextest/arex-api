package com.arextest.web.model.dto.filesystem;

import lombok.Data;

@Data
public class FSCaseDto extends FSInterfaceDto {
    private ComparisonMsgDto comparisonMsg;
    private String description;
}

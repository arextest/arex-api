package com.arextest.report.model.dto.filesystem;

import lombok.Data;

import java.util.Map;

@Data
public class FSNodeDto {
    private String nodeName;
    private Integer nodeType;
    private String infoId;
    private Map<String, FSNodeDto> children;
}

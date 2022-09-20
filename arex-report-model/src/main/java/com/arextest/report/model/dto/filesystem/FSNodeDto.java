package com.arextest.report.model.dto.filesystem;

import lombok.Data;

import java.util.List;

@Data
public class FSNodeDto {
    private String nodeName;
    private Integer nodeType;
    private String infoId;
    private String method;  // available for nodeType equal 1
    private List<FSNodeDto> children;
}

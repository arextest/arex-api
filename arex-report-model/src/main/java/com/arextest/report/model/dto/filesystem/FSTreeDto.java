package com.arextest.report.model.dto.filesystem;

import lombok.Data;

import java.util.List;

@Data
public class FSTreeDto {
    private String id;
    private String workspaceName;
    private String userName;
    private List<FSNodeDto> roots;
}

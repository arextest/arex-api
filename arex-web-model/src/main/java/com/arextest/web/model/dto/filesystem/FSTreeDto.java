package com.arextest.web.model.dto.filesystem;

import java.util.List;

import lombok.Data;

@Data
public class FSTreeDto {
    private String id;
    private String workspaceName;
    private String userName;
    private List<FSNodeDto> roots;
}

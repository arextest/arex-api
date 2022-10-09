package com.arextest.report.model.dto.filesystem.importexport;

import lombok.Data;

import java.util.List;

@Data
public class FolderItemDto implements Item {
    private String nodeName;
    private Integer nodeType;
    private List<Item> items;
}

package com.arextest.web.model.dto.filesystem.importexport;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class FolderItemDto implements Item {
    private String name;
    private String nodeName;
    private Integer nodeType;
    private Set<String> labelIds;
    private List<Item> items;
}

package com.arextest.web.core.business.filesystem.importexport.postmancollection;

import lombok.Data;

@Data
public class ItemHeader {
    private String key;
    private String value;
    private String type;
    private String name;
    private String description;
    private Boolean disabled;
}

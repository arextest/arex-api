package com.arextest.web.core.business.filesystem.importexport.postmancollection;

import java.util.List;

import lombok.Data;

@Data
public class ItemRequest {
    private String method;
    private List<ItemHeader> header;
    private ItemBody body;
    private ItemUrl url;
    private String description;
}

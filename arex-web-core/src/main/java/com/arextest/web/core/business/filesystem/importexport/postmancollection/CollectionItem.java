package com.arextest.web.core.business.filesystem.importexport.postmancollection;

import java.util.List;

import lombok.Data;

@Data
public class CollectionItem {
    private String name;
    private List<CollectionItem> item;
    private ItemRequest request;
    private List<ItemResponse> response;
    private List<CollectionEvent> event;
}

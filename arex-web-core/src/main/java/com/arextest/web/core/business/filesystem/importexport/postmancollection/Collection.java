package com.arextest.web.core.business.filesystem.importexport.postmancollection;

import lombok.Data;

import java.util.List;

@Data
public class Collection {
    private CollectionInfo info;
    private List<CollectionItem> item;
    private List<CollectionEvent> event;
}

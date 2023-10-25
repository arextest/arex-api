package com.arextest.web.core.business.filesystem.importexport.postmancollection;

import java.util.List;

import lombok.Data;

@Data
public class Collection {
    private CollectionInfo info;
    private List<CollectionItem> item;
    private List<CollectionEvent> event;
}

package com.arextest.report.model.dto.filesystem.importexport;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = InterfaceItemDto.class, name = "1"),
        @JsonSubTypes.Type(value = CaseItemDto.class, name = "2"),
        @JsonSubTypes.Type(value = FolderItemDto.class, name = "3")
})
public interface Item {
    List<Item> getItems();
    void setItems(List<Item> items);
}

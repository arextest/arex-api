package com.arextest.report.model.api.contracts.filesystem;

import lombok.Data;

import java.util.List;

@Data
public class FSRemoveItemRequestType {
    private String id;
    private String[] removeNodePath;
}

package com.arextest.report.model.api.contracts.filesystem;

import lombok.Data;

import java.util.List;

@Data
public class FSRenameRequestType {
    private String id;
    private String[] path;
    private String newName;
}

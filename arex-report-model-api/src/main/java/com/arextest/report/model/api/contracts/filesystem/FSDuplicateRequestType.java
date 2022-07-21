package com.arextest.report.model.api.contracts.filesystem;

import lombok.Data;

@Data
public class FSDuplicateRequestType {
    private String id;
    private String[] path;
}

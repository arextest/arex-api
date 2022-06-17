package com.arextest.report.model.api.contracts.filesystem;

import lombok.Data;

@Data
public class KeyValuePairType {
    private String key;
    private String value;
    private Boolean active;
}

package com.arextest.web.model.dto;

import lombok.Data;

@Data
public class KeyValuePairDto {
    private String key;
    private String value;
    private Boolean active;
}

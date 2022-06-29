package com.arextest.report.model.api.contracts.filesystem;

import lombok.Data;

@Data
public class BodyType {
    private String contentType;
    private String body;
}
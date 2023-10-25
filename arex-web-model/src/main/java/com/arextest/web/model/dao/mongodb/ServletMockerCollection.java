package com.arextest.web.model.dao.mongodb;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "ServletMocker")
public class ServletMockerCollection extends ModelBase {
    private String appId;
    private String path;
    private String request;
    private String response;
}

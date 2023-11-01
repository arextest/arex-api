package com.arextest.web.model.dao.mongodb;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "PreprocessConfig")
public class PreprocessConfigCollection extends ModelBase {
    private String name;
    private String index;
}

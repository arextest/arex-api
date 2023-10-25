package com.arextest.web.model.dao.mongodb;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "MessagePreprocess")
public class MessagePreprocessCollection extends ModelBase {
    private String key;
    private String path;
    private Long publishDate;
}

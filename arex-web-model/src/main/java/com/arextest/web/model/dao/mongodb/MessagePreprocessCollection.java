package com.arextest.web.model.dao.mongodb;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "MessagePreprocess")
public class MessagePreprocessCollection extends ModelBase {
    private String key;
    private String path;
    private Long publishDate;
}

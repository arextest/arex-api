package com.arextest.report.model.dao.mongodb;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "PreprocessConfig")
public class PreprocessConfigCollection extends ModelBase {
    private String name;
    private String index;
}

package com.arextest.web.model.dao.mongodb;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "Application")
public class ApplicationCollection extends ModelBase {
    private String operationId;
    private String operationName;
    private String operationType;
    private String contract;
}

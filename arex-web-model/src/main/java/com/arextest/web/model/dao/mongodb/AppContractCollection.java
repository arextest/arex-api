package com.arextest.web.model.dao.mongodb;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
@Document(collection = "AppContract")
public class AppContractCollection extends ModelBase {
    private String appId;
    private Integer contractType;
    private String operationId;
    private String operationName;
    private String operationType;
    private String contract;
}

package com.arextest.report.model.dao.mongodb;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@Document(collection = "ServiceOperation")
public class ServiceOperationCollection extends ModelBase {
    @NonNull
    private String appId;
    @NonNull
    private String serviceId;
    @NonNull
    private String operationName;

    private int operationType;
    @NonNull
    private Integer recordedCaseCount;
    @NonNull
    private Integer status;
}

//package com.arextest.web.model.dao.mongodb;
//
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.NonNull;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//import java.util.List;
//import java.util.Set;
//
//
//@Data
//@NoArgsConstructor
//@Document(collection = "ServiceOperation")
//public class ServiceOperationCollection extends ModelBase {
//    @NonNull
//    private String appId;
//    @NonNull
//    private String serviceId;
//    @NonNull
//    private String operationName;
//    // operation response used to convert to schema
//    private String operationResponse;
//    @Deprecated
//    private String operationType;
//    private Set<String> operationTypes;
//    @NonNull
//    private Integer recordedCaseCount;
//    @NonNull
//    private Integer status;
//}

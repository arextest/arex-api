//package com.arextest.web.model.dao.mongodb;
//
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.NonNull;
//import org.springframework.data.mongodb.core.index.Indexed;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//
//@Data
//@NoArgsConstructor
//@Document(collection = "App")
//public class AppCollection extends ModelBase {
//    @NonNull
//    @Indexed(unique = true)
//    private String appId;
//
//    private int features;
//
//    @NonNull
//    private String groupName;
//    @NonNull
//    private String groupId;
//    @NonNull
//    private String agentVersion;
//    @NonNull
//    private String agentExtVersion;
//    @NonNull
//    private String appName;
//    @NonNull
//    private String description;
//    @NonNull
//    private String category;
//    @NonNull
//    private String owner;
//    @NonNull
//    private String organizationName;
//    @NonNull
//    private Integer recordedCaseCount;
//    @NonNull
//    private String organizationId;
//    @NonNull
//    private Integer status;
//}

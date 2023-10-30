package com.arextest.web.model.dao.mongodb;

import java.util.Set;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "FSCase")
public class FSCaseCollection extends FSInterfaceCollection {
    private String recordId;
    // private ComparisonMsgDao comparisonMsg;
    private Set<String> labelIds;
    private Boolean inherited;
}

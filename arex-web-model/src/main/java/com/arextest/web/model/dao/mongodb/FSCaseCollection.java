package com.arextest.web.model.dao.mongodb;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@Document(collection = "FSCase")
public class FSCaseCollection extends FSInterfaceCollection {
    private String recordId;
    // private ComparisonMsgDao comparisonMsg;
    private Set<String> labelIds;
    private Boolean inherited;
}

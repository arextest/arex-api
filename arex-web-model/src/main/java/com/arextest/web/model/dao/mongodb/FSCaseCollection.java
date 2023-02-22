package com.arextest.web.model.dao.mongodb;

import com.arextest.web.model.dao.mongodb.entity.ComparisonMsgDao;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@Document(collection = "FSCase")
public class FSCaseCollection extends FSInterfaceCollection {
    private String recordId;
    private ComparisonMsgDao comparisonMsg;
    private Set<String> labelIds;
    private String description;
    private Boolean inherited;
}

package com.arextest.web.model.dao.mongodb;

import com.arextest.web.model.dao.mongodb.entity.AddressDao;
import com.arextest.web.model.dao.mongodb.entity.AuthDao;
import com.arextest.web.model.dao.mongodb.entity.BodyDao;
import com.arextest.web.model.dao.mongodb.entity.ComparisonMsgDao;
import com.arextest.web.model.dao.mongodb.entity.KeyValuePairDao;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Document(collection = "FSCase")
public class FSCaseCollection extends FSInterfaceCollection {
    private String recordId;
    private ComparisonMsgDao comparisonMsg;
    private Set<String> labelIds;
    private String description;
}

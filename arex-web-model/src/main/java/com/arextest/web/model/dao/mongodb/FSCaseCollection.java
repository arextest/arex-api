package com.arextest.web.model.dao.mongodb;

import com.arextest.web.model.dao.mongodb.entity.AddressDao;
import com.arextest.web.model.dao.mongodb.entity.AuthDao;
import com.arextest.web.model.dao.mongodb.entity.BodyDao;
import com.arextest.web.model.dao.mongodb.entity.ComparisonMsgDao;
import com.arextest.web.model.dao.mongodb.entity.KeyValuePairDao;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;

@Data
@Document(collection = "FSCase")
public class FSCaseCollection extends ModelBase {
    private String name;
    private String workspaceId;
    private AddressDao address;
    private String preRequestScript;
    private String testScript;
    private BodyDao body;
    private List<KeyValuePairDao> headers;
    private List<KeyValuePairDao> params;
    private AuthDao auth;
    private AddressDao testAddress;
    private String parentId;
    private Integer parentNodeType;
    private String recordId;
    private ComparisonMsgDao comparisonMsg;
    private Set<String> labelIds;
    private String description;
}

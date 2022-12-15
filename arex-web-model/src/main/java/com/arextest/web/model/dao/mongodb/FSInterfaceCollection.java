package com.arextest.web.model.dao.mongodb;

import com.arextest.web.model.dao.mongodb.entity.AddressDao;
import com.arextest.web.model.dao.mongodb.entity.AuthDao;
import com.arextest.web.model.dao.mongodb.entity.BodyDao;
import com.arextest.web.model.dao.mongodb.entity.KeyValuePairDao;
import com.arextest.web.model.dao.mongodb.entity.ScriptBlockDao;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@Document(collection = "FSInterface")
public class FSInterfaceCollection extends ModelBase {
    private String name;
    private String workspaceId;
    private AddressDao address;
    private List<ScriptBlockDao> preRequestScripts;
    private String testScript;
    private BodyDao body;
    private List<KeyValuePairDao> headers;
    private List<KeyValuePairDao> params;
    private AuthDao auth;
    private AddressDao testAddress;
    private String parentId;
    private Integer parentNodeType;
    private Map<String, Object> customTags;
    private String operationId;
}

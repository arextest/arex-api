package com.arextest.report.model.dao.mongodb;

import com.arextest.report.model.dao.mongodb.entity.AddressDao;
import com.arextest.report.model.dao.mongodb.entity.AuthDao;
import com.arextest.report.model.dao.mongodb.entity.BodyDao;
import com.arextest.report.model.dao.mongodb.entity.KeyValuePairDao;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "FSCase")
public class FSCaseCollection extends ModelBase {
    private String workspaceId;
    private AddressDao address;
    private String preRequestScript;
    private String testScript;
    private BodyDao body;
    private List<KeyValuePairDao> headers;
    private List<KeyValuePairDao> params;
    private AuthDao auth;
    private AddressDao baseAddress;
    private AddressDao testAddress;
    private String parentId;
    private Integer parentNodeType;
}

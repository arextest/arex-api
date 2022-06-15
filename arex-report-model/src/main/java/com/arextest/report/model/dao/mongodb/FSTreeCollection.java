package com.arextest.report.model.dao.mongodb;

import com.arextest.report.model.dao.mongodb.entity.FSNode;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Document(collection = "FSTreeCollection")
public class FSTreeCollection extends ModelBase {
    private String workspaceName;
    private String userName;
    private Map<String, FSNode> roots;
}

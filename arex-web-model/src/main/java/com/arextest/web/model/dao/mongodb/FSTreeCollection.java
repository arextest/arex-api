package com.arextest.web.model.dao.mongodb;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.arextest.web.model.dao.mongodb.entity.FSNode;

import lombok.Data;

@Data
@Document(collection = "FSTree")
public class FSTreeCollection extends ModelBase {
    private String workspaceName;
    private String userName;
    private List<FSNode> roots;
}

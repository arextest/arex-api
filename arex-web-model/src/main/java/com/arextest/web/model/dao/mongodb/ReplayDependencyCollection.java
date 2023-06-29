package com.arextest.web.model.dao.mongodb;

import com.arextest.web.model.dao.mongodb.entity.DependencyDao;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "ReplayDependency")
public class ReplayDependencyCollection extends ModelBase {
    private String operationId;
    private String operationName;
    private String operationType;
    private String recordId;
    private List<DependencyDao> dependencies;
}

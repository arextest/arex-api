package com.arextest.web.model.dao.mongodb;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@Document(collection = "Instances")
public class InstancesCollection extends ModelBase {

    @NonNull
    private String appId;

    private String host;

    private String recordVersion;

}

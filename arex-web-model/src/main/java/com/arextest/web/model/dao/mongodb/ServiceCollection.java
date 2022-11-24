package com.arextest.web.model.dao.mongodb;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@Document(collection = "Service")
public class ServiceCollection extends ModelBase {
    @NonNull
    private String appId;
    @NonNull
    private String serviceName;
    @NonNull
    private String serviceKey;
    @NonNull
    private Integer status;

}

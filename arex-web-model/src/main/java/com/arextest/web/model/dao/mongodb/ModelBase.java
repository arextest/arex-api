package com.arextest.web.model.dao.mongodb;

import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
public class ModelBase {
    @Id
    private String id;
    private Long dataChangeCreateTime;
    private Long dataChangeUpdateTime;
}

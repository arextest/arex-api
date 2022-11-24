package com.arextest.web.model.dao.mongodb;

import lombok.Data;
import org.springframework.data.annotation.Id;


@Data
public class ModelBase {
    @Id
    private String id;
    private Long dataChangeCreateTime;
    private Long dataChangeUpdateTime;
}

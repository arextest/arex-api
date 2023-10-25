package com.arextest.web.model.dao.mongodb;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author wildeslam.
 * @create 2023/9/25 15:45
 */
@Data
@FieldNameConstants
@Document(collection = "SystemConfig")
public class SystemConfigCollection extends ModelBase {

    private String operator;

    /**
     * for callBackInform.
     */
    private String callbackUrl;

}

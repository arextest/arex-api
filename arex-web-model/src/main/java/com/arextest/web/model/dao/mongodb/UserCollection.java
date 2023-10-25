package com.arextest.web.model.dao.mongodb;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
@Document(collection = "User")
public class UserCollection extends ModelBase {
    private String userName;
    private String verificationCode;
    private Long verificationTime;
    private String profile;
    private List<String> favoriteApps;
    private Integer status;
}

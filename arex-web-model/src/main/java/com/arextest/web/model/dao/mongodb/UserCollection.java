package com.arextest.web.model.dao.mongodb;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

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

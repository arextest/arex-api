package com.arextest.report.model.dao.mongodb;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "User")
public class UserCollection extends ModelBase {
    private String userName;
    private String verificationCode;
    private Long verificationTime;
    private String profile;
    private Integer status;
}

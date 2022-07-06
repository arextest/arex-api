package com.arextest.report.model.dao.mongodb;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "UserCollection")
public class UserCollection extends ModelBase {
    private String email;
    private String verificationCode;
}

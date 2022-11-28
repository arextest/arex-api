package com.arextest.web.model.dao.mongodb;

import com.arextest.web.model.contract.contracts.login.FavoriteApp;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "User")
public class UserCollection extends ModelBase {
    private String userName;
    private String verificationCode;
    private Long verificationTime;
    private String profile;
    private List<FavoriteApp> favoriteApps;
    private Integer status;
}

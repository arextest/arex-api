package com.arextest.web.model.contract.contracts.login;

import java.util.List;

import lombok.Data;

/**
 * Created by rchen9 on 2022/11/23.
 */
@Data
public class QueryUserFavoriteAppResponseType {
    private String userName;
    private List<String> favoriteApps;
}

package com.arextest.web.model.contract.contracts.login;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created by rchen9 on 2022/11/23.
 */
@Data
public class ModifyUserFavoriteAppRequestType {
    @NotBlank(message = "username cannot be blank")
    private String userName;
    @NotNull(message = "favoriteApp cannot be null")
    private FavoriteApp favoriteApp;
}

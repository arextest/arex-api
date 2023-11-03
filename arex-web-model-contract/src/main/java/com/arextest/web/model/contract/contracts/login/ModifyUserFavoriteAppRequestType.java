package com.arextest.web.model.contract.contracts.login;

import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Created by rchen9 on 2022/11/23.
 */
@Data
public class ModifyUserFavoriteAppRequestType {

  @NotBlank(message = "username cannot be blank")
  private String userName;
  @NotBlank(message = "favoriteApp cannot be blank")
  private String favoriteApp;
}

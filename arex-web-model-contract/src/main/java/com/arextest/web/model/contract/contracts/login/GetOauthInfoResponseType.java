package com.arextest.web.model.contract.contracts.login;

import lombok.Data;

/**
 * @author b_yu
 * @since 2023/8/17
 */
@Data
public class GetOauthInfoResponseType {

  private String clientId;
  private String oauthUri;
}

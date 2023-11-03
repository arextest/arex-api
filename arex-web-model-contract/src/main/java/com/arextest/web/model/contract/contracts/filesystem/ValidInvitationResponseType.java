package com.arextest.web.model.contract.contracts.filesystem;

import lombok.Data;

/**
 * Created by rchen9 on 2022/8/15.
 */
@Data
public class ValidInvitationResponseType {

  private boolean success;
  private String accessToken;
  private String refreshToken;
}

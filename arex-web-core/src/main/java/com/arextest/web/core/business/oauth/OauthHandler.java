package com.arextest.web.core.business.oauth;

import com.arextest.common.jwt.JWTService;
import com.arextest.web.core.business.LoginActivityService;
import com.arextest.web.core.repository.UserRepository;
import com.arextest.web.model.contract.contracts.login.GetOauthInfoResponseType;
import com.arextest.web.model.contract.contracts.login.VerifyResponseType;
import com.arextest.web.model.dto.UserDto;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author b_yu
 * @since 2023/8/16
 */
@Component
@RequiredArgsConstructor
public class OauthHandler {
  private final OauthServiceFactory oauthServiceFactory;
  private final UserRepository userRepository;
  private final JWTService jwtService;
  private final LoginActivityService loginActivityService;

  public VerifyResponseType oauthLogin(String code, String oauthType, String redirectUri) {
    OauthService oauthService = oauthServiceFactory.getOauthService(oauthType);
    VerifyResponseType response = new VerifyResponseType();

    String userName = oauthService.getUser(code, redirectUri);
    if (StringUtils.isBlank(userName)) {
      response.setSuccess(false);
      response.setReason("oauth login failed");
      return response;
    }

    UserDto userDto = new UserDto();
    userDto.setUserName(userName);
    loginActivityService.onEvent(userName, UserDto.ActivityType.LOGIN);
    userRepository.saveUser(userDto);

    response.setSuccess(true);
    response.setUserName(userName);
    response.setAccessToken(jwtService.makeAccessToken(userName));
    response.setRefreshToken(jwtService.makeRefreshToken(userName));

    return response;
  }

  public GetOauthInfoResponseType getOauthInfo(String oauthType) {
    GetOauthInfoResponseType response = new GetOauthInfoResponseType();
    OauthService oauthService = oauthServiceFactory.getOauthService(oauthType);
    response.setClientId(oauthService.getClientId());
    response.setOauthUri(oauthService.getOauthUri());
    return response;
  }
}

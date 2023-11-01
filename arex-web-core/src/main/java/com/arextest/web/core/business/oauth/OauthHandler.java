package com.arextest.web.core.business.oauth;

import com.arextest.common.utils.JwtUtil;
import com.arextest.web.core.repository.UserRepository;
import com.arextest.web.model.contract.contracts.login.GetOauthInfoResponseType;
import com.arextest.web.model.contract.contracts.login.VerifyResponseType;
import com.arextest.web.model.dto.UserDto;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author b_yu
 * @since 2023/8/16
 */
@Component
public class OauthHandler {

  @Resource
  private OauthServiceFactory oauthServiceFactory;
  @Resource
  private UserRepository userRepository;

  public VerifyResponseType oauthLogin(String code, String oauthType) {
    OauthService oauthService = oauthServiceFactory.getOauthService(oauthType);
    VerifyResponseType response = new VerifyResponseType();

    String userName = oauthService.getUser(code);
    if (StringUtils.isBlank(userName)) {
      response.setSuccess(false);
      response.setReason("oauth login failed");
      return response;
    }

    UserDto userDto = new UserDto();
    userDto.setUserName(userName);
    userRepository.saveUser(userDto);

    response.setSuccess(true);
    response.setUserName(userName);
    response.setAccessToken(JwtUtil.makeAccessToken(userName));
    response.setRefreshToken(JwtUtil.makeRefreshToken(userName));

    return response;
  }

  public GetOauthInfoResponseType getOauthInfo(String oauthType) {
    GetOauthInfoResponseType response = new GetOauthInfoResponseType();
    OauthService oauthService = oauthServiceFactory.getOauthService(oauthType);
    response.setClientId(oauthService.getClientId());
    response.setRedirectUri(oauthService.getRedirectUri());
    response.setOauthUri(oauthService.getOauthUri());
    return response;
  }
}

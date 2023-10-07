package com.arextest.web.api.service.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.config.model.dto.ModifyType;
import com.arextest.web.core.business.LoginService;
import com.arextest.web.core.business.oauth.OauthHandler;
import com.arextest.web.model.contract.contracts.SuccessResponseType;
import com.arextest.web.model.contract.contracts.login.GetOauthInfoResponseType;
import com.arextest.web.model.contract.contracts.login.LoginAsGuestRequestType;
import com.arextest.web.model.contract.contracts.login.LoginAsGuestResponseType;
import com.arextest.web.model.contract.contracts.login.ModifyUserFavoriteAppRequestType;
import com.arextest.web.model.contract.contracts.login.OauthLoginRequestType;
import com.arextest.web.model.contract.contracts.login.QueryUserFavoriteAppResponseType;
import com.arextest.web.model.contract.contracts.login.UpdateUserProfileRequestType;
import com.arextest.web.model.contract.contracts.login.UserProfileResponseType;
import com.arextest.web.model.contract.contracts.login.VerifyRequestType;
import com.arextest.web.model.contract.contracts.login.VerifyResponseType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/api/login/")
public class LoginController {

    @Resource
    private LoginService loginService;

    @Resource
    private OauthHandler oauthHandler;

    @GetMapping("/getVerificationCode/{userName}")
    @ResponseBody
    public Response sendVerifyCodeByEmail(@PathVariable String userName) {
        SuccessResponseType response = new SuccessResponseType();
        response.setSuccess(loginService.sendVerifyCodeByEmail(userName));
        return ResponseUtils.successResponse(response);
    }

    @PostMapping("/verify")
    @ResponseBody
    public Response verify(@Valid @RequestBody VerifyRequestType request) {
        VerifyResponseType response = loginService.verify(request);
        return ResponseUtils.successResponse(response);
    }

    @GetMapping("/userProfile/{userName}")
    @ResponseBody
    public Response userProfile(@PathVariable String userName) {
        UserProfileResponseType response = loginService.queryUserProfile(userName);
        return ResponseUtils.successResponse(response);
    }

    @PostMapping("/updateUserProfile")
    @ResponseBody
    public Response updateUserProfile(@Valid @RequestBody UpdateUserProfileRequestType request) {
        SuccessResponseType response = new SuccessResponseType();
        response.setSuccess(loginService.updateUserProfile(request));
        return ResponseUtils.successResponse(response);
    }

    @GetMapping("/refresh/{userName}")
    @ResponseBody
    public Response refresh(@PathVariable String userName) {
        VerifyResponseType response = loginService.refresh(userName);
        return ResponseUtils.successResponse(response);
    }

    @PostMapping("/loginAsGuest")
    @ResponseBody
    public Response loginAsGuest(@RequestBody LoginAsGuestRequestType request) {
        LoginAsGuestResponseType response = loginService.loginAsGuest(request.getUserName());
        return ResponseUtils.successResponse(response);
    }

    @GetMapping("/userFavoriteApp/{userName}")
    @ResponseBody
    public Response queryUserFavoriteApp(@PathVariable String userName) {
        QueryUserFavoriteAppResponseType response = loginService.queryUserFavoriteApp(userName);
        return ResponseUtils.successResponse(response);
    }

    @PostMapping("/userFavoriteApp/modify/{modifyType}")
    @ResponseBody
    public Response modifyUserFavoriteApp(@PathVariable ModifyType modifyType,
            @Valid @RequestBody ModifyUserFavoriteAppRequestType request) {
        if (modifyType == ModifyType.INSERT) {
            return ResponseUtils.successResponse(loginService.insertUserFavoriteApp(request));
        }
        if (modifyType == ModifyType.REMOVE) {
            return ResponseUtils.successResponse(loginService.removeUserFavoriteApp(request));
        }
        return ResponseUtils.resourceNotFoundResponse();
    }

    @PostMapping("/oauthLogin")
    @ResponseBody
    public Response oauthLogin(@RequestBody OauthLoginRequestType request) {
        VerifyResponseType response = oauthHandler.oauthLogin(request.getCode(), request.getOauthType());
        return ResponseUtils.successResponse(response);
    }

    @GetMapping("/oauthInfo/{oauthType}")
    @ResponseBody
    public Response getOauthClientId(@PathVariable String oauthType) {
        GetOauthInfoResponseType response = oauthHandler.getOauthInfo(oauthType);
        return ResponseUtils.successResponse(response);
    }

    @GetMapping("/listVerifiedUser")
    @ResponseBody
    public Response listAllUsers() {
        return ResponseUtils.successResponse(loginService.listVerifiedUser());
    }
}

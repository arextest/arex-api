package com.arextest.web.api.service.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.LoginService;
import com.arextest.web.model.contract.contracts.SuccessResponseType;
import com.arextest.web.model.contract.contracts.login.LoginAsGuestRequestType;
import com.arextest.web.model.contract.contracts.login.LoginAsGuestResponseType;
import com.arextest.web.model.contract.contracts.login.UpdateUserProfileRequestType;
import com.arextest.web.model.contract.contracts.login.UserProfileResponseType;
import com.arextest.web.model.contract.contracts.login.VerifyRequestType;
import com.arextest.web.model.contract.contracts.login.VerifyResponseType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@Controller
@RequestMapping("/api/login/")
public class LoginController {

    @Resource
    private LoginService loginService;

    @GetMapping("/getVerificationCode/{userName}")
    @ResponseBody
    public Response sendVerifyCodeByEmail(@PathVariable String userName) {
        if (StringUtils.isEmpty(userName)) {
            return ResponseUtils.errorResponse("userName cannot be empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            SuccessResponseType response = new SuccessResponseType();
            response.setSuccess(loginService.sendVerifyCodeByEmail(userName));
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/verify")
    @ResponseBody
    public Response verify(@RequestBody VerifyRequestType request) {
        if (StringUtils.isEmpty(request.getUserName())) {
            return ResponseUtils.errorResponse("userName cannot be empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        if (StringUtils.isEmpty(request.getVerificationCode())) {
            return ResponseUtils.errorResponse("verification code cannot be empty",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            VerifyResponseType response = loginService.verify(request);
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @GetMapping("/userProfile/{userName}")
    @ResponseBody
    public Response userProfile(@PathVariable String userName) {
        if (StringUtils.isEmpty(userName)) {
            return ResponseUtils.errorResponse("userName cannot be empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            UserProfileResponseType response = loginService.queryUserProfile(userName);
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/updateUserProfile")
    @ResponseBody
    public Response updateUserProfile(@RequestBody UpdateUserProfileRequestType request) {
        if (StringUtils.isEmpty(request.getUserName())) {
            return ResponseUtils.errorResponse("userName cannot be empty",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            SuccessResponseType response = new SuccessResponseType();
            response.setSuccess(loginService.updateUserProfile(request));
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @GetMapping("/refresh/{userName}")
    @ResponseBody
    public Response refresh(@PathVariable String userName) {
        if (StringUtils.isEmpty(userName)) {
            return ResponseUtils.errorResponse("userName cannot be empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            VerifyResponseType response = loginService.refresh(userName);
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/loginAsGuest")
    @ResponseBody
    public Response loginAsGuest(@RequestBody LoginAsGuestRequestType request) {
        try {
            LoginAsGuestResponseType response = loginService.loginAsGuest(request.getUserName());
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }
}

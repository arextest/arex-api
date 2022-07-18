package com.arextest.report.web.api.service.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.report.core.business.LoginService;
import com.arextest.report.model.api.contracts.SuccessResponseType;
import com.arextest.report.model.api.contracts.login.UpdateUserProfileRequestType;
import com.arextest.report.model.api.contracts.login.UserProfileResponseType;
import com.arextest.report.model.api.contracts.login.VerifyRequestType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Slf4j
@Controller
@RequestMapping("/api/login/")
public class LoginController {

    @Resource
    private LoginService loginService;

    @GetMapping("/getVerificationCode/{email}")
    @ResponseBody
    public Response sendVerifyCodeByEmail(@PathVariable String email) {
        if (StringUtils.isEmpty(email)) {
            return ResponseUtils.errorResponse("email cannot be empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            SuccessResponseType response = new SuccessResponseType();
            response.setSuccess(loginService.sendVerifyCodeByEmail(email));
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/verify")
    @ResponseBody
    public Response verify(@RequestBody VerifyRequestType request) {
        if (StringUtils.isEmpty(request.getEmail())) {
            return ResponseUtils.errorResponse("email cannot be empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        if (StringUtils.isEmpty(request.getVerificationCode())) {
            return ResponseUtils.errorResponse("verification code cannot be empty",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            SuccessResponseType response = new SuccessResponseType();
            response.setSuccess(loginService.verify(request));
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @GetMapping("/userProfile/{email}")
    @ResponseBody
    public Response userProfile(@PathVariable String email) {
        if (StringUtils.isEmpty(email)) {
            return ResponseUtils.errorResponse("email cannot be empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            UserProfileResponseType response = loginService.queryUserProfile(email);
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/updateUserProfile")
    @ResponseBody
    public Response updateUserProfile(@RequestBody UpdateUserProfileRequestType request) {
        if (StringUtils.isEmpty(request.getEmail())) {
            return ResponseUtils.errorResponse("user name(email) cannot be empty",
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
}

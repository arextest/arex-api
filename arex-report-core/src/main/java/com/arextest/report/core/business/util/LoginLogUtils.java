package com.arextest.report.core.business.util;

import com.arextest.report.common.HttpUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoginLogUtils {
    @Value("${arex.email.domain}")
    private String arexEmailDomain;

    private String loginLogUrl;

    public boolean loginLog(String userName) {
        if (StringUtils.isEmpty(loginLogUrl)) {
            loginLogUrl = arexEmailDomain + "/login/log";
        }
        LoginLogRequest request = new LoginLogRequest(userName);
        ResponseEntity<LoginLogResponse> response =
                HttpUtils.post(loginLogUrl, request, LoginLogResponse.class);
        return response.getBody().getData().getSuccess();
    }

    @Data
    public static class LoginLogRequest {
        private String userName;

        public LoginLogRequest(String userName) {
            this.userName = userName;
        }
    }


    @Data
    public static class LoginLogResponse {
        private Body data;
    }


    @Data
    public static class Body {
        private Boolean success;
    }
}

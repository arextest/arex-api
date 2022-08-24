package com.arextest.report.core.business;

import com.arextest.report.common.JwtUtil;
import com.arextest.report.common.LoadResource;
import com.arextest.report.core.business.util.MailUtils;
import com.arextest.report.core.repository.UserRepository;
import com.arextest.report.model.api.contracts.login.LoginAsGuestResponseType;
import com.arextest.report.model.api.contracts.login.UpdateUserProfileRequestType;
import com.arextest.report.model.api.contracts.login.UserProfileResponseType;
import com.arextest.report.model.api.contracts.login.VerifyRequestType;
import com.arextest.report.model.api.contracts.login.VerifyResponseType;
import com.arextest.report.model.dto.UserDto;
import com.arextest.report.model.enums.UserStatusType;
import com.arextest.report.model.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Random;

@Slf4j
@Component
public class LoginService {

    private static final String SEND_VERIFICATION_CODE_SUBJECT = "[ArexTest] Verification code";
    private static final String VERIFICATION_CODE_PLACEHOLDER = "{{verificationCode}}";
    private static final String VERIFICATION_CODE_EMAIL_TEMPLATE = "classpath:verificationCodeEmailTemplate.htm";
    private static final String GUEST_PREFIX = "GUEST_";

    @Resource
    private UserRepository userRepository;

    @Resource
    private LoadResource loadResource;

    @Resource
    private MailUtils mailUtils;

    public Boolean sendVerifyCodeByEmail(String emailTo) {
        String template = loadResource.getResource(VERIFICATION_CODE_EMAIL_TEMPLATE);
        UserDto user = new UserDto();
        user.setUserName(emailTo);
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationTime(System.currentTimeMillis());
        boolean success = userRepository.saveUser(user);
        if (success) {
            template = template.replace(VERIFICATION_CODE_PLACEHOLDER, user.getVerificationCode());
            success = success & mailUtils.sendEmail(user.getUserName(),
                    SEND_VERIFICATION_CODE_SUBJECT,
                    template);
        }
        return success;
    }

    public VerifyResponseType verify(VerifyRequestType request) {
        VerifyResponseType responseType = new VerifyResponseType();

        boolean exist = userRepository.verify(request.getUserName(), request.getVerificationCode());

        if (exist) {
            UserDto userDto = new UserDto();
            userDto.setUserName(request.getUserName());
            userDto.setVerificationCode(generateVerificationCode());
            exist = exist & userRepository.saveUser(userDto);
        }
        if (exist) {
            responseType.setSuccess(true);
            responseType.setAccessToken(JwtUtil.makeAccessToken(request.getUserName()));
            responseType.setRefreshToken(JwtUtil.makeRefreshToken(request.getUserName()));
        } else {
            responseType.setSuccess(false);
        }
        return responseType;
    }

    public UserProfileResponseType queryUserProfile(String userName) {
        return UserMapper.INSTANCE.contractFromDto(userRepository.queryUserProfile(userName));
    }

    public Boolean updateUserProfile(UpdateUserProfileRequestType request) {
        UserDto dto = UserMapper.INSTANCE.dtoFromContract(request);
        return userRepository.updateUserProfile(dto);
    }

    public VerifyResponseType refresh(String userName) {
        VerifyResponseType responseType = new VerifyResponseType();
        responseType.setSuccess(true);
        responseType.setAccessToken(JwtUtil.makeAccessToken(userName));
        responseType.setRefreshToken(JwtUtil.makeRefreshToken(userName));
        return responseType;
    }

    public LoginAsGuestResponseType loginAsGuest(String userName) {
        LoginAsGuestResponseType response = new LoginAsGuestResponseType();
        if (StringUtils.isEmpty(userName)) {
            // retry 3 times
            for (int i = 0; i < 3; i++) {
                String guestName = generateGuestUserName();
                if (userRepository.existUserName(guestName)) {
                    continue;
                }
                userName = guestName;
                break;
            }
        }
        if (StringUtils.isEmpty(userName)) {
            response.setSuccess(false);
            return response;
        }
        UserDto user = new UserDto();
        user.setUserName(userName);
        user.setStatus(UserStatusType.GUEST);
        Boolean result = userRepository.saveUser(user);
        if (result) {
            response.setUserName(userName);
            response.setSuccess(true);
            response.setAccessToken(JwtUtil.makeAccessToken(userName));
            response.setRefreshToken(JwtUtil.makeRefreshToken(userName));
        } else {
            response.setSuccess(false);
        }
        return response;
    }

    private String generateVerificationCode() {
        StringBuilder verificationCode = new StringBuilder(6);
        Random r = new Random();
        for (int i = 0; i < 6; i++) {
            verificationCode.append(r.nextInt(10));
        }
        return verificationCode.toString();
    }

    private String generateGuestUserName() {
        StringBuilder guestName = new StringBuilder(20);
        guestName.append(GUEST_PREFIX);
        Random r = new Random();
        for (int i = 0; i < 15; i++) {
            guestName.append(UserStatusType.VALID_USERNAME_CHAR[r.nextInt(UserStatusType.VALID_USERNAME_CHAR.length)]);
        }
        return guestName.toString();
    }
}

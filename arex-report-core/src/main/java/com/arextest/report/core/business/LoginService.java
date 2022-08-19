package com.arextest.report.core.business;

import com.arextest.report.common.JwtUtil;
import com.arextest.report.common.LoadResource;
import com.arextest.report.core.business.util.MailUtils;
import com.arextest.report.core.repository.UserRepository;
import com.arextest.report.model.api.contracts.login.UpdateUserProfileRequestType;
import com.arextest.report.model.api.contracts.login.UserProfileResponseType;
import com.arextest.report.model.api.contracts.login.VerifyRequestType;
import com.arextest.report.model.api.contracts.login.VerifyResponseType;
import com.arextest.report.model.dto.UserDto;
import com.arextest.report.model.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Random;

@Slf4j
@Component
public class LoginService {

    private static final String SEND_VERIFICATION_CODE_SUBJECT = "[ArexTest] Verification code";
    private static final String VERIFICATION_CODE_PLACEHOLDER = "{{verificationCode}}";
    private static final String VERIFICATION_CODE_EMAIL_TEMPLATE = "classpath:verificationCodeEmailTemplate.htm";

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
        boolean success = userRepository.saveVerificationCode(user);
        if (success) {
            template = template.replace(VERIFICATION_CODE_PLACEHOLDER, user.getVerificationCode());
            success = success & mailUtils.sendEmail(user.getUserName(),
                    SEND_VERIFICATION_CODE_SUBJECT,
                    template,
                    true);
        }
        return success;
    }

    public VerifyResponseType verify(VerifyRequestType request) {
        VerifyResponseType responseType = new VerifyResponseType();

        boolean exist = saveUserName(request.getUserName()) &&
                userRepository.verify(request.getUserName(), "000000");

        if (exist) {
            UserDto userDto = new UserDto();
            userDto.setUserName(request.getUserName());
            userDto.setVerificationCode(generateVerificationCode());
            exist = exist & userRepository.saveVerificationCode(userDto);
            if (exist & userRepository.saveVerificationCode(userDto)) {
                responseType.setSuccess(true);
                responseType.setAccessToken(JwtUtil.makeAccessToken(request.getUserName()));
                responseType.setRefreshToken(JwtUtil.makeRefreshToken(request.getUserName()));
            }
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

    private boolean saveUserName(String userName) {
        UserDto user = new UserDto();
        user.setUserName(userName);
        user.setVerificationCode("000000");
        user.setVerificationTime(System.currentTimeMillis());
        return userRepository.saveVerificationCode(user);
    }

    private String generateVerificationCode() {
        StringBuilder verificationCode = new StringBuilder(6);
        Random r = new Random();
        for (int i = 0; i < 6; i++) {
            verificationCode.append(r.nextInt(10));
        }
        return verificationCode.toString();
    }


}

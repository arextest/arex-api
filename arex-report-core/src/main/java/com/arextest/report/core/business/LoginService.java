package com.arextest.report.core.business;

import com.arextest.report.core.business.util.MailUtils;
import com.arextest.report.core.repository.UserRepository;
import com.arextest.report.model.api.contracts.VerifyRequestType;
import com.arextest.report.model.dto.UserDto;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.security.auth.Subject;
import java.util.Random;

@Component
public class LoginService {

    private static final String SEND_VERIFICATION_CODE_SUBJECT = "[ArexTest] verification code";
    private static final String SEND_VERIFICATION_CODE_MSG = "Verification Code is: %s";

    @Resource
    private UserRepository userRepository;

    @Resource
    private MailUtils mailUtils;

    public Boolean sendVerifyCodeByEmail(String emailTo) {
        UserDto user = new UserDto();
        user.setEmail(emailTo);
        user.setVerificationCode(generateVerificationCode());
        boolean success = userRepository.saveVerificationCode(user);
        if (success) {
            success = success & mailUtils.sendEmail(user.getEmail(),
                    SEND_VERIFICATION_CODE_SUBJECT,
                    String.format(SEND_VERIFICATION_CODE_MSG, user.getVerificationCode()),
                    true);
        }
        return success;
    }

    public Boolean verify(VerifyRequestType request) {
        boolean exist = userRepository.verify(request.getEmail(), request.getVerificationCode());
        if (exist) {
            UserDto userDto = new UserDto();
            userDto.setEmail(request.getEmail());
            userDto.setVerificationCode(generateVerificationCode());
            exist = exist & userRepository.saveVerificationCode(userDto);
        }
        return exist;
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

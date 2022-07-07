package com.arextest.report.core.repository;


import com.arextest.report.model.dto.UserDto;

public interface UserRepository extends RepositoryProvider {
    Boolean saveVerificationCode(UserDto user);
    Boolean verify(String email, String verificationCode);
}

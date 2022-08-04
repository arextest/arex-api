package com.arextest.report.core.repository;


import com.arextest.report.model.dto.UserDto;

public interface UserRepository extends RepositoryProvider {
    Boolean saveVerificationCode(UserDto user);
    Boolean verify(String userName, String verificationCode);
    UserDto queryUserProfile(String userName);
    Boolean updateUserProfile(UserDto user);
}

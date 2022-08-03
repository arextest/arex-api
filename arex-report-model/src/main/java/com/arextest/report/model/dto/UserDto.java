package com.arextest.report.model.dto;

import lombok.Data;

@Data
public class UserDto {
    private String id;
    private String userName;
    private String verificationCode;
    private Long verificationTime;
    private String profile;
}

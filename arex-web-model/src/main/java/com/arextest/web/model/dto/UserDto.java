package com.arextest.web.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private String id;
    private String userName;
    private String verificationCode;
    private Long verificationTime;
    private String profile;
    private List<String> favoriteApps;
    private Integer status;
}

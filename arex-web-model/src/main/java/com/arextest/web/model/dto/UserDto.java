package com.arextest.web.model.dto;

import java.util.List;

import lombok.Data;

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

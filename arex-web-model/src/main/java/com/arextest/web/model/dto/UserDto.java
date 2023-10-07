package com.arextest.web.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserDto extends BaseUserDto {
    private String verificationCode;
    private Long verificationTime;
    private String profile;
    private List<String> favoriteApps;
    private Integer status;
}

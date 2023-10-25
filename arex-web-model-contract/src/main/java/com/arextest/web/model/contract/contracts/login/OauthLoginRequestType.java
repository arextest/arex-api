package com.arextest.web.model.contract.contracts.login;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * @author b_yu
 * @since 2023/8/15
 */
@Data
public class OauthLoginRequestType {
    @NotBlank(message = "oauthType cannot be blank")
    private String oauthType;
    @NotBlank(message = "code cannot be blank")
    private String code;
}

package com.arextest.web.core.business.ai.impls.gpt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenGPTConfig {
    private String endpoint;
    private String token;
    private String model;
    private Integer maxToken;
}
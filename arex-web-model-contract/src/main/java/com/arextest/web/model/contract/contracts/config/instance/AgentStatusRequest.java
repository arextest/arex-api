package com.arextest.web.model.contract.contracts.config.instance;

import lombok.Data;

/**
 * @author Owen_gan
 * @since 2023/7/17
 */
@Data
public class AgentStatusRequest {
    String appId;
    String host;
    String status;
}

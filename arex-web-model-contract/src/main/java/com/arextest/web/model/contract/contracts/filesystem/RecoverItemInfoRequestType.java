package com.arextest.web.model.contract.contracts.filesystem;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author b_yu
 * @since 2023/2/7
 */
@Data
public class RecoverItemInfoRequestType {
    @NotBlank(message = "Recovery Id cannot be empty")
    private String recoveryId;
}

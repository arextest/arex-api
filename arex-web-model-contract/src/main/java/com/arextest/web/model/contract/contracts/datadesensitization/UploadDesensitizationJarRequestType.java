package com.arextest.web.model.contract.contracts.datadesensitization;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UploadDesensitizationJarRequestType {
    @NotBlank(message = "Jar url can not be empty")
    private String jarUrl;
    private String remark;
}

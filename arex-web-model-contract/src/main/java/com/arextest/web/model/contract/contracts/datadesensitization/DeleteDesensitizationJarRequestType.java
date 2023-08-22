package com.arextest.web.model.contract.contracts.datadesensitization;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DeleteDesensitizationJarRequestType {
    @NotBlank(message = "id can not be empty")
    private String id;
}

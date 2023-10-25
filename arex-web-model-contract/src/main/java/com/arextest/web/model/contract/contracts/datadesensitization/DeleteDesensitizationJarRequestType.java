package com.arextest.web.model.contract.contracts.datadesensitization;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class DeleteDesensitizationJarRequestType {
    @NotBlank(message = "id can not be empty")
    private String id;
}

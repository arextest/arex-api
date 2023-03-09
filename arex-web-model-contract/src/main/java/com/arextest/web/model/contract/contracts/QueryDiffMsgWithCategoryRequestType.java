package com.arextest.web.model.contract.contracts;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Created by rchen9 on 2023/3/8.
 */
@Data
public class QueryDiffMsgWithCategoryRequestType {
    @NotBlank(message = "recordId cannot be empty")
    private String recordId;
    @NotBlank(message = "replayId cannot be empty")
    private String replayId;
    @NotBlank(message = "categoryName cannot be empty")
    private String categoryName;
}

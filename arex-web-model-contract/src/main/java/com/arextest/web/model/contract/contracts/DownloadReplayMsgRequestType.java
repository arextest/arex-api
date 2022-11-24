package com.arextest.web.model.contract.contracts;

import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
public class DownloadReplayMsgRequestType {
    @NotBlank(message = "DownloadReplayMsg id cannot be empty")
    private String id;
    
    private boolean baseMsgDownload;
}

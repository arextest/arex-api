package com.arextest.report.model.api.contracts;

import lombok.Data;


@Data
public class DownloadReplayMsgRequestType {
    private String id;
    
    private boolean baseMsgDownload;
}

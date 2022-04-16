package io.arex.report.model.api.contracts;

import lombok.Data;


@Data
public class DownloadReplayMsgRequestType {
    private String id;
    
    private boolean baseMsgDownload;
}

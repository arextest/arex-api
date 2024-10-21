package com.arextest.web.model.contract.contracts;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DownloadReplayMsgRequestType {

  @NotBlank(message = "DownloadReplayMsg id cannot be empty")
  private String id;

  private boolean baseMsgDownload;
}

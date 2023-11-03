package com.arextest.web.model.dto.filesystem;

import java.util.List;
import lombok.Data;

/**
 * @author b_yu
 * @since 2023/1/18
 */
@Data
public class FSTraceLogDto {

  private String id;
  private int traceType;
  private String userName;
  private String workspaceId;
  private String infoId;
  private String parentId;
  private FSNodeDto node;
  private List<FSItemDto> items;
  private long dataChangeCreateTime;
}

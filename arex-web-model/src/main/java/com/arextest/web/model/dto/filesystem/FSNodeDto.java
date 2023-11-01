package com.arextest.web.model.dto.filesystem;

import java.util.List;
import java.util.Set;
import lombok.Data;

@Data
public class FSNodeDto {

  private String nodeName;
  private Integer nodeType;
  private String infoId;
  private String method; // available for nodeType equal 1
  /**
   * available for nodeType equal 2
   *
   * @see com.arextest.web.model.enums.CaseSourceType
   */
  private int caseSourceType;
  private Set<String> labelIds;
  private List<FSNodeDto> children;
}

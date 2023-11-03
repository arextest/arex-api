package com.arextest.web.model.contract.contracts.filesystem;

import com.arextest.web.model.contract.contracts.common.KeyValuePairType;
import com.arextest.web.model.contract.contracts.common.ScriptBlockType;
import java.util.List;
import java.util.Set;
import lombok.Data;

@Data
public class FSQueryCaseResponseType extends FSQueryItemType {

  private String workspaceId;
  private AddressType address;
  private List<ScriptBlockType> preRequestScripts;
  private List<ScriptBlockType> testScripts;
  private BodyType body;
  private List<KeyValuePairType> headers;
  private List<KeyValuePairType> params;
  private AuthType auth;
  private AddressType testAddress;
  private String recordId;
  // private ComparisonMsgType comparisonMsg;
  private Set<String> labelIds;
  private String description;
  private Boolean inherited;
  private List<ScriptBlockType> parentPreRequestScripts;
}

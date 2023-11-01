package com.arextest.web.model.dto.filesystem.importexport;

import com.arextest.web.model.dto.KeyValuePairDto;
import com.arextest.web.model.dto.filesystem.AddressDto;
import com.arextest.web.model.dto.filesystem.AuthDto;
import com.arextest.web.model.dto.filesystem.BodyDto;
import com.arextest.web.model.dto.filesystem.ScriptBlockDto;
import java.util.List;
import java.util.Set;
import lombok.Data;

@Data
public class CaseItemDto implements Item {

  private String name;
  private String nodeName;
  private Integer nodeType;
  private Integer caseSourceType;
  private AddressDto address;
  private List<ScriptBlockDto> preRequestScripts;
  private List<ScriptBlockDto> testScripts;
  private BodyDto body;
  private List<KeyValuePairDto> headers;
  private List<KeyValuePairDto> params;
  private AuthDto auth;
  private AddressDto testAddress;
  private Set<String> labelIds;
  private List<Item> items;
}

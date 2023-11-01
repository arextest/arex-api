package com.arextest.web.model.contract.contracts.manualreport;

import com.arextest.web.model.contract.contracts.filesystem.AddressType;
import java.util.List;
import lombok.Data;

@Data
public class ReportInterfaceType {

  List<ReportCaseIdNameType> reportCases;
  private String id;
  private String interfaceName;
  private AddressType testAddress;
}

package com.arextest.web.model.contract.contracts.manualreport;

import com.arextest.web.model.contract.contracts.filesystem.AddressType;
import lombok.Data;

import java.util.List;

@Data
public class ReportInterfaceType {
    private String id;
    private String interfaceName;
    private AddressType testAddress;
    List<ReportCaseIdNameType> reportCases;
}

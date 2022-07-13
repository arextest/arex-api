package com.arextest.report.model.api.contracts.manualreport;

import com.arextest.report.model.api.contracts.filesystem.AddressType;
import lombok.Data;

import java.util.List;

@Data
public class ReportInterfaceType {
    private String id;
    private String interfaceName;
    private AddressType baseAddress;
    private AddressType testAddress;
    List<ReportCaseIdNameType> reportCases;
}

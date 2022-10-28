package com.arextest.report.model.dto.manualreport;

import com.arextest.report.model.dao.mongodb.entity.AddressDao;
import lombok.Data;

@Data
public class ManualReportPlanItemDto {
    private String id;
    private String planId;
    private String interfaceName;
    private AddressDao testAddress;
}

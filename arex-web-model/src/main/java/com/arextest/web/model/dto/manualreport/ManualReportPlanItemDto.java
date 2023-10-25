package com.arextest.web.model.dto.manualreport;

import com.arextest.web.model.dao.mongodb.entity.AddressDao;

import lombok.Data;

@Data
public class ManualReportPlanItemDto {
    private String id;
    private String planId;
    private String interfaceName;
    private AddressDao testAddress;
}

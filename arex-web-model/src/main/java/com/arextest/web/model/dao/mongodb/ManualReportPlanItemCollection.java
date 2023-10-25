package com.arextest.web.model.dao.mongodb;

import org.springframework.data.mongodb.core.mapping.Document;

import com.arextest.web.model.dao.mongodb.entity.AddressDao;

import lombok.Data;

@Data
@Document(collection = "ManualReportPlanItem")
public class ManualReportPlanItemCollection extends ModelBase {
    private String planId;
    private String interfaceName;
    private AddressDao testAddress;
}

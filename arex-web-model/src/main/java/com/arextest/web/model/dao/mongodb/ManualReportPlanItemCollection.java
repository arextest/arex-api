package com.arextest.web.model.dao.mongodb;

import com.arextest.web.model.dao.mongodb.entity.AddressDao;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "ManualReportPlanItem")
public class ManualReportPlanItemCollection extends ModelBase {

  private String planId;
  private String interfaceName;
  private AddressDao testAddress;
}

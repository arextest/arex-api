package com.arextest.web.model.contract.contracts;

import lombok.Data;

/**
 * @author wildeslam.
 * @create 2023/12/8 16:37
 */
@Data
public class QueryPlanStatisticRequestType {

      private String appId;
      private String planId;
      private String imageId;
}

package com.arextest.web.model.contract.contracts;

import java.util.List;
import lombok.Data;

@Data
public class QueryAllAppIdResponseType {

  private List<String> appIds;
}

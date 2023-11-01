package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.contracts.common.Difference;
import java.util.List;
import lombok.Data;

@Data
public class QueryDifferencesResponseType {

  private List<Difference> differences;
}

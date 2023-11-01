package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.contracts.common.Scene;
import java.util.List;
import lombok.Data;

@Data
public class QueryScenesResponseType {

  private List<Scene> scenes;
}

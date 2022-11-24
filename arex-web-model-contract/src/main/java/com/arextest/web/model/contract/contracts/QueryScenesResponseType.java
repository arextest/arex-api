package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.contracts.common.Scene;
import lombok.Data;

import java.util.List;


@Data
public class QueryScenesResponseType {

    private List<Scene> scenes;
}

package com.arextest.web.model.contract.contracts;

import java.util.List;

import com.arextest.web.model.contract.contracts.common.Scene;

import lombok.Data;

@Data
public class QueryScenesResponseType {

    private List<Scene> scenes;
}

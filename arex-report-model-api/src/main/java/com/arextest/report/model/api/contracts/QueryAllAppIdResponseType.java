package com.arextest.report.model.api.contracts;

import lombok.Data;

import java.util.List;


@Data
public class QueryAllAppIdResponseType {

    private List<String> appIds;
}

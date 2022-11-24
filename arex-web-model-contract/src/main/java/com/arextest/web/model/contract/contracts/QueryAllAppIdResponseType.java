package com.arextest.web.model.contract.contracts;

import lombok.Data;

import java.util.List;


@Data
public class QueryAllAppIdResponseType {

    private List<String> appIds;
}

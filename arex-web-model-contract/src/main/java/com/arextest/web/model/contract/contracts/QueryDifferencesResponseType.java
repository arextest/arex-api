package com.arextest.web.model.contract.contracts;

import java.util.List;

import com.arextest.web.model.contract.contracts.common.Difference;

import lombok.Data;

@Data
public class QueryDifferencesResponseType {

    private List<Difference> differences;
}

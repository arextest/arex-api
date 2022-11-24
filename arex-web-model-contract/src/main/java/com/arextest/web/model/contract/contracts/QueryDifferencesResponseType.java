package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.contracts.common.Difference;
import lombok.Data;

import java.util.List;


@Data
public class QueryDifferencesResponseType {

    private List<Difference> differences;
}

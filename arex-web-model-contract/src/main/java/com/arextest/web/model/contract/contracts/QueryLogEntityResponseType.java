package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.contracts.common.LogEntity;

import lombok.Data;

/**
 * Created by rchen9 on 2023/4/12.
 */
@Data
public class QueryLogEntityResponseType {
    private int diffResultCode;
    private LogEntity logEntity;
}

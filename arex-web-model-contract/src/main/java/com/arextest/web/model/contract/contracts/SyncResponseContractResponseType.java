package com.arextest.web.model.contract.contracts;

import lombok.Data;

import java.util.Map;

@Data
public class SyncResponseContractResponseType {
    private String contractStr;
    private Map<String, String> dependenciesContractMap;
}

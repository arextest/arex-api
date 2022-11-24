package com.arextest.web.model.contract.contracts.compare;

import lombok.Data;

/**
 * Created by rchen9 on 2022/7/1.
 */
@Data
public class MsgCombination {
    private String caseId;
    private String baseMsg;
    private String testMsg;
}

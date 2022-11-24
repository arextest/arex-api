package com.arextest.web.model.contract.contracts.compare;

import lombok.Data;

/**
 * Created by rchen9 on 2022/7/8.
 */
@Data
public class ExceptionMsg {
    String caseId;
    String baseMsg;
    String testMsg;
    String remark;
}

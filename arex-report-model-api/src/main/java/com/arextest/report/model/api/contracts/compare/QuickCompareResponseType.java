package com.arextest.report.model.api.contracts.compare;

import com.arextest.report.model.api.contracts.common.LogEntity;
import lombok.Data;

import java.util.List;

/**
 * Created by rchen9 on 2022/7/1.
 */
@Data
public class QuickCompareResponseType {
    private int diffResultCode;
    private String baseMsg;
    private String testMsg;
    private List<LogEntity> logs;
}

package com.arextest.report.core.business.compare;

import com.arextest.diff.model.CompareResult;
import com.arextest.diff.sdk.CompareSDK;
import com.arextest.report.model.api.contracts.compare.MsgCombination;
import com.arextest.report.model.api.contracts.compare.QuickCompareResponseType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Created by rchen9 on 2022/6/30.
 */
@Slf4j
@Component
public class CompareService {

    public QuickCompareResponseType quickCompare(MsgCombination msgCombination) {
        QuickCompareResponseType quickCompareResponseType = new QuickCompareResponseType();
        CompareSDK compareSDK = new CompareSDK();
        CompareResult compareResult = compareSDK.compare(msgCombination.getBaseMsg(), msgCombination.getTestMsg());
        quickCompareResponseType.setDiffResultCode(compareResult.getCode());
        quickCompareResponseType.setBaseMsg(compareResult.getProcessedBaseMsg());
        quickCompareResponseType.setTestMsg(compareResult.getProcessedTestMsg());
        if (compareResult.getLogs() != null) {
            quickCompareResponseType.setLogs(compareResult.getLogs().stream()
                    .map(LogEntityMapper.INSTANCE::fromLogEntity).collect(Collectors.toList()));
        }
        return quickCompareResponseType;
    }
}

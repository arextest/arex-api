package com.arextest.web.model.contract.contracts.compare;

import lombok.Data;

import java.util.List;

/**
 * Created by rchen9 on 2022/7/1.
 */
@Data
public class AggCompareRequestType {
    private List<MsgCombination> msgCombinations;
}

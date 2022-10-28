package com.arextest.report.model.api.contracts.filesystem;

import com.arextest.report.model.api.contracts.compare.DiffDetail;
import lombok.Data;

import java.util.List;

/**
 * Created by rchen9 on 2022/10/27.
 */
@Data
public class ComparisonMsgType {
    private int diffResultCode;
    private String baseMsg;
    private String testMsg;
    private List<DiffDetail> diffDetails;
}

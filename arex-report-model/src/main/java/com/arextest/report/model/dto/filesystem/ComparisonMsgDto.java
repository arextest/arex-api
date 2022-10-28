package com.arextest.report.model.dto.filesystem;

import com.arextest.report.model.api.contracts.compare.DiffDetail;
import lombok.Data;

import java.util.List;

@Data
public class ComparisonMsgDto {
    private int diffResultCode;
    private String baseMsg;
    private String testMsg;
    private List<DiffDetail> diffDetails;
}
package com.arextest.report.model.dao.mongodb.entity;

import com.arextest.report.model.api.contracts.compare.DiffDetail;
import lombok.Data;

import java.util.List;
@Data
public class ComparisonMsgDao {
    private int diffResultCode;
    private String baseMsg;
    private String testMsg;
    private List<DiffDetail> diffDetails;
}
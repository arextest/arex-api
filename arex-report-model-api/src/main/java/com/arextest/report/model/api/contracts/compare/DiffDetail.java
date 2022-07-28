package com.arextest.report.model.api.contracts.compare;

import com.arextest.report.model.api.contracts.common.LogEntity;
import lombok.Data;

import java.util.List;

/**
 * Created by rchen9 on 2022/7/28.
 */
@Data
public class DiffDetail {
    private String path;
    private int unmatchedType;
    private List<LogEntity> logs;
}

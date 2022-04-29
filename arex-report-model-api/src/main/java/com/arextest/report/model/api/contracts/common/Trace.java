package com.arextest.report.model.api.contracts.common;

import lombok.Data;

import java.util.List;


@Data
public class Trace {
    private List<List<NodeEntity>> currentTraceLeft;
    private List<List<NodeEntity>> currentTraceRight;
}

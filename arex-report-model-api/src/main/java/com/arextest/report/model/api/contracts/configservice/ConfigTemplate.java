package com.arextest.report.model.api.contracts.configservice;

import lombok.Data;


@Data
public class ConfigTemplate {
    private RecordConfig recordConfig;
    private ReplayConfig replayConfig;
    private CompareConfig compareConfig;
}

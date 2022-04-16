package io.arex.report.model.api.contracts.configservice;

import lombok.Data;


@Data
public class ConfigTemplate {
    private RecordConfig recordConfig;
    private ReplayConfig replayConfig;
    private CompareConfig compareConfig;
}

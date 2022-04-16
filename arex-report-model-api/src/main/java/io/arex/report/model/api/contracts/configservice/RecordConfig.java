package io.arex.report.model.api.contracts.configservice;

import lombok.Data;

import java.util.List;


@Data
public class RecordConfig {
    private ServiceCollect serviceCollection;
    private List<DynamicClass> dynamicClass;
}

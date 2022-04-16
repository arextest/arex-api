package io.arex.report.model.api.contracts.configservice;

import lombok.Data;

import java.util.Collection;
import java.util.Set;


@Data
public class ServiceCollect {
    
    private int sampleRate;

    private Set<String> excludeDependentOperationSet;

    
    private Collection<String> excludeDependentServiceSet;
    
    private Collection<String> excludeOperationSet;

    
    private Collection<String> includeServiceSet;
    
    private Collection<String> includeOperationSet;
    
    private int allowDayOfWeeks;
    
    private String allowTimeOfDayFrom;
    
    private String allowTimeOfDayTo;
}

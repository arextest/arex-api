package com.arextest.report.model.dao.mongodb;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;


@Data
@NoArgsConstructor
@Document(collection = "RecordServiceConfig")
public class RecordServiceConfigCollection extends ModelBase {

    @NonNull
    @Indexed(unique = true)
    private String appId;

    private int sampleRate;

    private Set<String> excludeDependentOperationSet;

    private Set<String> excludeDependentServiceSet;

    private Set<String> excludeOperationSet;

    private Set<String> includeServiceSet;

    private Set<String> includeOperationSet;

    private int allowDayOfWeeks;

    private boolean timeMock;
    @NonNull
    private String allowTimeOfDayFrom;
    @NonNull
    private String allowTimeOfDayTo;
}

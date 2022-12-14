package com.arextest.web.model.dao.mongodb;

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

    private int allowDayOfWeeks;

    private boolean timeMock;
    @NonNull
    private String allowTimeOfDayFrom;
    @NonNull
    private String allowTimeOfDayTo;

    private Set<String> excludeServiceOperationSet;
}

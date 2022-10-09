package com.arextest.report.model.dao.mongodb;

import com.arextest.report.model.dao.mongodb.entity.AbstractComparisonDetails;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "ConfigComparisonInclusions")
public class ConfigComparisonInclusionsCollection extends AbstractComparisonDetails {

    @NonNull
    private List<String> inclusions;

}

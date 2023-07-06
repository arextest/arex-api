package com.arextest.web.model.dao.mongodb;

import com.arextest.web.model.dao.mongodb.entity.AbstractComparisonDetails;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Document(collection = "ConfigComparisonInclusions")
public class ConfigComparisonInclusionsCollection extends AbstractComparisonDetails {

    private List<String> inclusions;

}

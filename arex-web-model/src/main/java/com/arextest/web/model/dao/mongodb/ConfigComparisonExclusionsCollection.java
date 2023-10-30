package com.arextest.web.model.dao.mongodb;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.arextest.web.model.dao.mongodb.entity.AbstractComparisonDetails;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Data
@NoArgsConstructor
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Document(collection = "ConfigComparisonExclusions")
public class ConfigComparisonExclusionsCollection extends AbstractComparisonDetails {
    private List<String> exclusions;
}

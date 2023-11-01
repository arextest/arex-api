package com.arextest.web.model.dao.mongodb;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.arextest.web.model.dao.mongodb.entity.AbstractComparisonDetails;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Data
@NoArgsConstructor
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Document(collection = "ConfigComparisonEncryption")
public class ConfigComparisonEncryptionCollection extends AbstractComparisonDetails {
    private List<String> path;
}

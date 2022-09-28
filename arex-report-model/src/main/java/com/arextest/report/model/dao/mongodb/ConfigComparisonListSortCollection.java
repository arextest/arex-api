package com.arextest.report.model.dao.mongodb;

import com.arextest.report.model.dao.mongodb.entity.AbstractComparisonDetails;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Data
@NoArgsConstructor
@Document(collection = "ConfigComparisonListSort")
public class ConfigComparisonListSortCollection extends AbstractComparisonDetails {
    private List<String> listPath;
    private List<List<String>> keys;
}

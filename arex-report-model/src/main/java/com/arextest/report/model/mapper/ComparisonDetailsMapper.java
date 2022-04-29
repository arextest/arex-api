package com.arextest.report.model.mapper;

import com.arextest.report.model.api.contracts.configservice.ComparisonDetails;
import com.arextest.report.model.api.contracts.configservice.ComparisonDetailsConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;


@Mapper
public interface ComparisonDetailsMapper {

    ComparisonDetailsMapper INSTANCE = Mappers.getMapper(ComparisonDetailsMapper.class);

    ComparisonDetails detailsFormConfig(ComparisonDetailsConfiguration comparisonDetailsConfiguration);

    ComparisonDetailsConfiguration configFromDetails(ComparisonDetails comparisonDetails);

    default String map(List<String> pathValue) {
        StringBuilder result = new StringBuilder();
        for (String str : pathValue) {
            result.append(str);
            result.append(",");
        }
        return result.length() == 0 ? result.toString() : result.substring(0, result.length() - 1);
    }

    default List<String> map(String pathValue) {
        if (StringUtils.isEmpty(pathValue)){
            return null;
        }
        return Arrays.asList(pathValue.split(","));
    }

}

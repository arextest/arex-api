package com.arextest.report.model.api.contracts.config.replay;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ComparisonExclusionsConfiguration extends AbstractComparisonDetailsConfiguration {
    List<String> exclusions;
}
package io.arex.report.model.api.contracts.configservice;


import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ComparisonDetailsConfiguration  {
    private long id;
    private String pathName;
    private List<String> pathValue;
}

package com.arextest.web.model.contract.contracts.label;

import lombok.Data;

import java.util.List;

/**
 * @author b_yu
 * @since 2022/11/22
 */
@Data
public class QueryLabelsByWorkspaceIdResponseType {
    private List<LabelType> labels;
}

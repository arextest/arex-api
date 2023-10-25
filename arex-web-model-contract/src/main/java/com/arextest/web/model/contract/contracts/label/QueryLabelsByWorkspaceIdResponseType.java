package com.arextest.web.model.contract.contracts.label;

import java.util.List;

import lombok.Data;

/**
 * @author b_yu
 * @since 2022/11/22
 */
@Data
public class QueryLabelsByWorkspaceIdResponseType {
    private List<LabelType> labels;
}

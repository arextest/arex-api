package com.arextest.report.model.api.contracts.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeEntity{
    private String nodeName;
    private int index;
}

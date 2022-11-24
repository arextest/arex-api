package com.arextest.web.model.contract.contracts.common;

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

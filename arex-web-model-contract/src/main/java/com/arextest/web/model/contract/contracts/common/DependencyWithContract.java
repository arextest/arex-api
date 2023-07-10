package com.arextest.web.model.contract.contracts.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DependencyWithContract extends Dependency{
    private String contract;
}

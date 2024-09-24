package com.arextest.web.model.contract.contracts.common;

import lombok.Data;

/**
 * @author b_yu
 * @since 2022/12/8
 */
@Data
public class ScriptBlockType {

    private String type;
    private String icon;
    private String label;
    private String value;
    private String description;
    private Boolean disabled;
}

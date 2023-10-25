package com.arextest.web.model.contract.contracts.config.yamlTemplate.entity;

import java.util.List;

import lombok.Data;

/**
 * Created by rchen9 on 2022/9/27.
 */
@Data
public class ListSortTemplateConfig {
    private String listPath;
    private List<String> keys;
}

package com.arextest.web.model.contract.contracts.filesystem;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author b_yu
 * @since 2023/9/25
 */
@Data
public class FSQueryItemType {
    private String id;
    private String name;
    private Map<String, Object> customTags;
    private List<ParentNodeType> parentPath;

    @Data
    public static class ParentNodeType {
        private String id;
        private String name;
        private Integer nodeType;
    }
}

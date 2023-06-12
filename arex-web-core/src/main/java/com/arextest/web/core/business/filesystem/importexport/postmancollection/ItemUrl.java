package com.arextest.web.core.business.filesystem.importexport.postmancollection;

import lombok.Data;

import java.util.List;

@Data
public class ItemUrl {
    private String raw;
    private String protocol;
    private List<String> host;
    private String port;
    private List<String> path;
    private List<ItemUrlQuery> query;

    @Data
    public static class ItemUrlQuery {
        private String key;
        private String value;
        private String description;
    }
}
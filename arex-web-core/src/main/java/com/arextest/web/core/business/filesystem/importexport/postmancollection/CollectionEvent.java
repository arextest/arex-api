package com.arextest.web.core.business.filesystem.importexport.postmancollection;

import java.util.List;

import lombok.Data;

@Data
public class CollectionEvent {
    /**
     * prerequest or test.
     */
    private String listen;
    private Script script;

    @Data
    public static class Script {
        private String id;
        private String type;
        private List<String> exec;
    }
}

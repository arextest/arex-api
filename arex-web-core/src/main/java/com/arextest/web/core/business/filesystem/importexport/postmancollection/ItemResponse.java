package com.arextest.web.core.business.filesystem.importexport.postmancollection;

import lombok.Data;

import java.util.List;

@Data
public class ItemResponse {
    private String name;
    private ItemRequest originalRequest;
    private String status;
    private Integer code;
    private String _postman_previewlanguage;
    private List<ItemHeader> header;
    private String body;
}

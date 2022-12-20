package com.arextest.web.model.contract.contracts.filesystem;

import lombok.Data;

import java.util.Map;

/**
 * Created by rchen9 on 2022/12/19.
 */
@Data
public class FSQueryFolderResponseType {
    private String id;
    private String name;
    private Map<String, Object> customTags;

}

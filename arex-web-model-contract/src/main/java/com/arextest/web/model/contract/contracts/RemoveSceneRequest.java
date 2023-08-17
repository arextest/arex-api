package com.arextest.web.model.contract.contracts;

import lombok.Data;

import java.util.List;

/**
 * @author wildeslam.
 * @create 2023/8/17 15:28
 */
@Data
public class RemoveSceneRequest {
    private List<String> planItemIdList;
}

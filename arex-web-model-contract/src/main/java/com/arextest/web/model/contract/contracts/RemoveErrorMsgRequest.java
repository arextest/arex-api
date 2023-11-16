package com.arextest.web.model.contract.contracts;

import lombok.Data;

import java.util.List;

/**
 * @author wildeslam.
 * @create 2023/11/16 16:37
 */
@Data
public class RemoveErrorMsgRequest {
    private String planId;
    private List<String> planItemIdList;
}

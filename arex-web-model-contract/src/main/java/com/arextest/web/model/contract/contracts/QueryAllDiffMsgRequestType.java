package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.PagingRequest;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Created by rchen9 on 2023/3/20.
 */
@Data
public class QueryAllDiffMsgRequestType implements PagingRequest {

    @NotBlank(message = "planItemId cannot be empty")
    private String planItemId;

    @NotBlank(message = "recordId cannot be empty")
    private String recordId;

    private List<Integer> diffResultCodeList;

    private Integer pageIndex;
    private Integer pageSize;
    private Boolean needTotal;
}

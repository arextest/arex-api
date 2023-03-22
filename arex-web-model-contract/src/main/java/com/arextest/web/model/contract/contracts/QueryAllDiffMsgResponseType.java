package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.PagingResponse;
import lombok.Data;

import java.util.List;

/**
 * Created by rchen9 on 2023/3/20.
 */
@Data
public class QueryAllDiffMsgResponseType implements PagingResponse {
    private List<CompareResultDetail> compareResultDetailList;
    private Long totalCount;
}

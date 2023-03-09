package com.arextest.web.model.contract.contracts;

import lombok.Data;

import java.util.List;

/**
 * Created by rchen9 on 2023/3/8.
 */
@Data
public class QueryFullLinkSummaryResponseType {
    List<FullLinkSummaryDetail> details;
}

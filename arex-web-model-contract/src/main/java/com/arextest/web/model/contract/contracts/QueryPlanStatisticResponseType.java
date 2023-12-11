package com.arextest.web.model.contract.contracts;

import com.arextest.model.response.Response;
import com.arextest.model.response.ResponseStatusType;
import com.arextest.web.model.contract.contracts.common.PlanStatistic;
import lombok.Data;

/**
 * @author wildeslam.
 * @create 2023/12/8 16:41
 */
@Data
public class QueryPlanStatisticResponseType implements Response {
    private PlanStatistic planStatistic;
    private ResponseStatusType responseStatusType;
}

package com.arextest.web.model.contract.contracts;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author wildeslam.
 * @create 2024/5/6 20:19
 */
@Data
public class DeletePlanItemStatisticsRequestType {
    private String planId;

    @Size(min = 1, message = "planItemIds can not be empty")
    private List<String> planItemIds;
}

package com.arextest.web.model.contract.contracts;

import lombok.Data;

/**
 * @author wildeslam.
 * @create 2023/9/8 11:32
 */
@Data
public class FeedbackSceneRequest {
    private String planId;
    private String planItemId;
    /**
     * @see com.arextest.web.model.enums.FeedbackTypeEnum
     */
    private Integer feedbackType;
    private String recordId;
    private String remark;
}

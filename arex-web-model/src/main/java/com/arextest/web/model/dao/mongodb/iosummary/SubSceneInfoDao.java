package com.arextest.web.model.dao.mongodb.iosummary;

import com.arextest.web.model.enums.FeedbackTypeEnum;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.util.List;

/**
 * Created by rchen9 on 2023/2/28.
 */
@Data
@FieldNameConstants
public class SubSceneInfoDao {
    private int code;
    private int count;

    private String recordId;
    private String replayId;
    /**
     * @see FeedbackTypeEnum#getCode() ;
     */
    private Integer feedbackType;

    private List<DiffDetailDao> details;
}

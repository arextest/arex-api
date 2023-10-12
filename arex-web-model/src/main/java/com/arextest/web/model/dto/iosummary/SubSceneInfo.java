package com.arextest.web.model.dto.iosummary;

import com.arextest.web.model.enums.FeedbackTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SubSceneInfo {

    private int code;
    private int count;

    private String recordId;
    private String replayId;
    /**
     * @see FeedbackTypeEnum#getCode() ;
     */
    private Integer feedbackType;

    private List<DiffDetail> details;

    public SubSceneInfo(int code, String recordId, String replayId, List<DiffDetail> details) {
        this.code = code;
        this.recordId = recordId;
        this.replayId = replayId;
        this.details = details;
    }
}

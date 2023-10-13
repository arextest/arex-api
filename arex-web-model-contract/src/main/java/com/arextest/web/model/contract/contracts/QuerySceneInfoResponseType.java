package com.arextest.web.model.contract.contracts;

import lombok.Data;

import java.util.List;

/**
 * Created by rchen9 on 2023/3/7.
 */
@Data
public class QuerySceneInfoResponseType {

    private List<SceneInfoType> sceneInfos;

    @Data
    public static class SceneInfoType {
        private int count;
        private List<SubSceneInfoType> subScenes;
    }

    @Data
    public static class SubSceneInfoType {
        private int count;
        private String recordId;
        private String replayId;
        private long recordTime;
        private long replayTime;
        /**
         * @see com.arextest.web.model.enums.FeedbackTypeEnum#getCode() ;
         */
        private Integer feedbackType;
        private List<DiffDetailType> details;
    }

    @Data
    public static class DiffDetailType {
        private int code;
        private String categoryName;
        private String operationName;
    }
}

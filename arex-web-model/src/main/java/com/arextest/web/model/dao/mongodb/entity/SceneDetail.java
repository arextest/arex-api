package com.arextest.web.model.dao.mongodb.entity;

import lombok.Data;

import java.util.List;


@Data
public class SceneDetail {
    private String compareResultId;
    private String logIndexes;
    private Integer sceneCount;
}

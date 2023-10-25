package com.arextest.web.model.dao.mongodb.iosummary;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.arextest.web.model.dao.mongodb.ModelBase;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

/**
 * Created by rchen9 on 2023/2/28.
 */
@Data
@NoArgsConstructor
@FieldNameConstants
@Document(collection = "SceneInfo")
public class SceneInfoCollection extends ModelBase {
    private int code;
    private int count;
    private long categoryKey;

    private String planId;
    private String planItemId;
    private List<SubSceneInfoDao> subScenes;
    private Map<String, SubSceneInfoDao> subSceneInfoMap;

    private Date dataCreateTime;
}

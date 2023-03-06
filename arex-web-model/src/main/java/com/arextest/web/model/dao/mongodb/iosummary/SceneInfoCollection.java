package com.arextest.web.model.dao.mongodb.iosummary;

import com.arextest.web.model.dto.iosummary.SubSceneInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Created by rchen9 on 2023/2/28.
 */
@Data
@NoArgsConstructor
@Document(collection = "SceneInfo")
public class SceneInfoCollection {
    private int code;
    private int count;
    private String planId;
    private String planItmId;
    private List<SubSceneInfo> subScenes;
}

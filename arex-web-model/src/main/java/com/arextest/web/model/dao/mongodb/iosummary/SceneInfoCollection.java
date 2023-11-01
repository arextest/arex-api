package com.arextest.web.model.dao.mongodb.iosummary;

import com.arextest.web.model.dao.mongodb.ModelBase;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

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
  private boolean reCalculated;
  private Date dataCreateTime;
}

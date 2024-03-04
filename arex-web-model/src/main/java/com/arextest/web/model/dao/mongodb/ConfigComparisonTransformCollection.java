package com.arextest.web.model.dao.mongodb;

import com.arextest.web.model.dao.mongodb.entity.AbstractComparisonDetails;
import com.arextest.web.model.dao.mongodb.entity.TransformDetailDao;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Data
@NoArgsConstructor
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Document(collection = "ConfigComparisonTransform")
public class ConfigComparisonTransformCollection extends AbstractComparisonDetails {

  TransformDetailDao transformDetail;

}

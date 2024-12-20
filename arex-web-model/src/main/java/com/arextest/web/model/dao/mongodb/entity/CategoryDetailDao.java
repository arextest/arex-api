package com.arextest.web.model.dao.mongodb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

/**
 * @author wildeslam.
 * @create 2024/1/5 15:03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class CategoryDetailDao {

      private String operationType;

      private String operationName;
}

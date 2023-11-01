package com.arextest.web.model.dao.mongodb.iosummary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by rchen9 on 2023/2/28.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiffDetailDao {

  int code;
  String categoryName;
  String operationName;
}

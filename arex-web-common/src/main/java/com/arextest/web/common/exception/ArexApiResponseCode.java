package com.arextest.web.common.exception;

import com.arextest.common.model.response.ResponseCode_New;

/**
 * Response codes from arex-api start with 1
 * @author b_yu
 * @since 2023/11/8
 */
public class ArexApiResponseCode extends ResponseCode_New {
  // common error codes start with 101xxx
  public static final int UNSUPPORTED_CATEGORY = 101001;
  public static final int RECORD_CASE_NOT_FOUND = 101002;

  // configuration error codes start with 102xxx
  public static final int LIST_KEY_CIRCLE_ERROR = 102001;

  // collections error codes start with 103xxx
  public static final int FS_DUPLICATE_ITEM_ERROR = 103001;
  public static final int FS_FORMAT_ERROR = 103002;

  // record & replay error codes start with 104xxx
}

package com.arextest.web.common.exception;

import com.arextest.common.exceptions.ArexException;

/**
 * @author b_yu
 * @since 2023/11/7
 */
public class UnsupportedCategoryArexException extends ArexException {

  public UnsupportedCategoryArexException(String message) {
    super(ArexApiResponseCode.UNSUPPORTED_CATEGORY, message);
  }
}

package com.arextest.web.common.exception;

import com.arextest.common.exceptions.ArexException;

/**
 * @author b_yu
 * @since 2023/11/8
 */
public class RecordCaseNotFoundArexException extends ArexException {

  public RecordCaseNotFoundArexException(String message) {
    super(ArexApiResponseCode.RECORD_CASE_NOT_FOUND, message);
  }
}

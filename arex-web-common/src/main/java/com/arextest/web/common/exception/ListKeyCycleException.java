package com.arextest.web.common.exception;

public class ListKeyCycleException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ListKeyCycleException(String message) {
    super(message);
  }
}
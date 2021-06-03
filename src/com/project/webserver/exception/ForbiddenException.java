package com.project.webserver.exception;

public class ForbiddenException extends HttpException {
  public ForbiddenException() {
    super(403, "Forbidden");
  }
}

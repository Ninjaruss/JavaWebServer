package com.project.webserver.exception;

public class UnauthorizedException extends HttpException {
  public UnauthorizedException() {
    super(401, "Unauthorized");
  }
}

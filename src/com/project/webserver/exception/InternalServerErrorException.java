package com.project.webserver.exception;

public class InternalServerErrorException extends HttpException {
  public InternalServerErrorException() {
    super(500, "Internal Server Error");
  }
}

package com.project.webserver.server;

/**
 * The class represent HTTP Response.
 * @author  Harsh Saxena
 * @version 1.0
 * @since   2020-09-08
 */
public class HttpResponse {


    private int statusCode;
    private String reasonPhrase;
    private StringBuilder responseHeader;

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }

    private byte[] fileContent;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    public StringBuilder getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(StringBuilder responseHeader) {
        this.responseHeader = responseHeader;
    }
}
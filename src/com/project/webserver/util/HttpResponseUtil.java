package com.project.webserver.util;

import com.project.webserver.exception.HttpException;
import com.project.webserver.exception.InternalServerErrorException;
import com.project.webserver.server.HttpRequest;
import com.project.webserver.server.HttpResource;
import com.project.webserver.server.HttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * The class implement utilities to process HTTP Response .
 * @author  Harsh Saxena
 * @version 1.0
 * @since   2020-09-08
 */
public class HttpResponseUtil {

    private final MimeTypesConfUtil mimeTypesConfUtil;
    private byte[] fileContent;

    public HttpResponseUtil(MimeTypesConfUtil mimeTypesConfUtil) {
        this.mimeTypesConfUtil = mimeTypesConfUtil;
    }

    /**
     * This method returns HTTP Response.
     * @return HttpResponse HTTP Response.
     */
    public HttpResponse getResponse(HttpRequest httpRequest, HttpResource httpResource) {

        HttpResponse httpResponse = new HttpResponse();

        boolean contentExist = false;
        String contentType = "text/text";


        if(httpRequest.isScriptAliased()){

            try{
                executeScript(httpRequest, httpResource);

                httpResponse.setStatusCode(200);
                String reasonPhrase = getReasonPhrase(200);
                httpResponse.setReasonPhrase(reasonPhrase);
                httpResponse.setFileContent(fileContent);
                contentType = "text/html";
                contentExist = true;

            }
            catch(IOException ex){
                httpResponse.setStatusCode(500);
                String reasonPhrase = getReasonPhrase(500);
                httpResponse.setReasonPhrase(reasonPhrase);
            }

        }
        else{

            int httpStatusCode = processHTTPVerbRequest(httpRequest, httpResource);

            httpResponse.setStatusCode(httpStatusCode);
            String reasonPhrase = getReasonPhrase(httpStatusCode);
            httpResponse.setReasonPhrase(reasonPhrase);


            if(httpStatusCode == 200){

                String[] pathContents = httpResource.getFile().getAbsolutePath().split("\\.");
                String fileExtension = pathContents[pathContents.length - 1];
                contentType = mimeTypesConfUtil.getMimeType(fileExtension);
                httpResponse.setFileContent(fileContent);
                contentExist = true;

            }

        }



        StringBuilder headers = new StringBuilder();
        Date localDate = new Date();

        headers.append(httpRequest.getHttpVersion());
        headers.append(" ");
        headers.append(httpResponse.getStatusCode());
        headers.append(" ");
        headers.append(httpResponse.getReasonPhrase());
        headers.append("\n");
        headers.append("Date: ");
        headers.append(localDate);
        headers.append("\n");
        headers.append("Status: " + httpResponse.getStatusCode() + " " + httpResponse.getReasonPhrase());
        headers.append("\n");

        if(contentExist){
            headers.append("Last-Modified: " + httpResource.getLastModified());
            headers.append("\n");
            headers.append("Content-Type: " + contentType);
            headers.append("\n");
            headers.append("Content-Length: " + fileContent.length);
            headers.append("\n");
        }

        headers.append("\n");

        httpResponse.setResponseHeader(headers);
        return httpResponse;

    }


    /**
     * This method return the HTTP Response
     * @return HttpResponse http response.
     */
    public HttpResponse getResponse(HtaccessConfUtil htaccessConfUtil, HttpException ex) {


        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatusCode(ex.getStatusCode());
        httpResponse.setReasonPhrase(ex.getMessage());

        StringBuilder headers = new StringBuilder();
        Date localDate = new Date();
        String field = "WWW-Authenticate";
        String value = htaccessConfUtil.getAuthType() + " realm=\"" +
                htaccessConfUtil.getAuthName() + "\"";

        headers.append("HTTP/1.1");
        headers.append(" ");
        headers.append(httpResponse.getStatusCode());
        headers.append(" ");
        headers.append(httpResponse.getReasonPhrase());
        headers.append("\n");
        headers.append("Date: ");
        headers.append(localDate);
        headers.append("\n");
        headers.append("Status: " + httpResponse.getStatusCode() + " " + httpResponse.getReasonPhrase());
        headers.append("\n");
        headers.append(field + ": " + value);
        headers.append("\n");
        headers.append("\n");

        httpResponse.setResponseHeader(headers);
        return httpResponse;

    }

    /**
     * This method return the HTTP Response based on exception
     * @return HttpResponse http response.
     */
    public HttpResponse getResponse(HttpException ex) {


        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatusCode(ex.getStatusCode());
        httpResponse.setReasonPhrase(ex.getMessage());

        StringBuilder headers = new StringBuilder();
        Date localDate = new Date();

        headers.append("HTTP/1.1");
        headers.append(" ");
        headers.append(httpResponse.getStatusCode());
        headers.append(" ");
        headers.append(httpResponse.getReasonPhrase());
        headers.append("\n");
        headers.append("Date: ");
        headers.append(localDate);
        headers.append("\n");
        headers.append("Status: " + httpResponse.getStatusCode() + " " + httpResponse.getReasonPhrase());
        headers.append("\n");
        headers.append("\n");

        httpResponse.setResponseHeader(headers);
        return httpResponse;

    }


    /**
     * This method process the http request based on HTTP Verb
     * @return int status code.
     */
    public int processHTTPVerbRequest(HttpRequest httpRequest, HttpResource httpResource){

        int httpStatusCode = 0;

        switch (httpRequest.getHttpVerb()) {
            case "GET":
                if (!httpResource.isExist()) httpStatusCode = 404;
                else {
                    try {
                        if (httpRequest.getHttpHeaders().containsKey("If-Modified-Since")){
                            DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                            Date existingDate = dateFormat.parse(httpRequest.getHttpHeaders().get("If-Modified-Since"));
                            Date lastModifiedDate = dateFormat.parse(httpResource.getLastModified());
                            if (existingDate.compareTo(lastModifiedDate) < 0){
                                fileContent = Files.readAllBytes(httpResource.getFile().toPath());
                                httpStatusCode = 200;
                            }
                            else{
                                httpStatusCode = 304;
                            }
                        }
                        else{
                            fileContent = Files.readAllBytes(httpResource.getFile().toPath());
                            httpStatusCode = 200;
                        }
                    }
                    catch (Exception e) {
                        httpStatusCode = 500;
                    }
                }
                break;

            case "PUT":
                if (httpResource.isExist()) httpStatusCode = 400;
                else {
                    if(httpResource.createFile()) httpStatusCode = 201;
                    else httpStatusCode = 500;
                }
                break;

            case "DELETE":
                if (!httpResource.isExist()) httpStatusCode = 404;
                else {
                    if(httpResource.deleteFile()) httpStatusCode = 204;
                    else httpStatusCode = 500;
                }
                break;

            default:
                httpStatusCode = 501;
                break;
        }

        return httpStatusCode;

    }


    /**
     * This method generates reason phrase based on HTTP Response Code
     * @return String Reason Phrase.
     */
    public static String getReasonPhrase(int httpStatusCode){

        String reasonPhrase = null;

        switch (httpStatusCode) {
            case 200:
                reasonPhrase = "OK";
                break;
            case 201:
               reasonPhrase = "Created";
               break;
            case 204:
                reasonPhrase = "No Content";
                break;
            case 304:
                reasonPhrase = "Not Modified";
                break;
            case 400:
                reasonPhrase = "Bad Request";
                break;
            case 401:
                reasonPhrase = "Unauthorized";
                break;
            case 403:
                reasonPhrase = "Forbidden";
                break;
            case 404:
                reasonPhrase = "Not Found";
                break;
            case 500:
                reasonPhrase = "Internal Server Error";
                break;
        }

        return reasonPhrase;

    }

    /**
     * This method send HTTP Response to the client.
     * @args  outputStream Output stream send to client.
     * @args  httpResponse HTTP Response.
     * @return Nothing.
     * @exception IOException on writing stream.
     */
    public void sendResponse(OutputStream outputStream, HttpResponse httpResponse) throws HttpException {

        try{

            StringBuilder headers = httpResponse.getResponseHeader();
            outputStream.write(headers.toString().getBytes());

            if(httpResponse.getStatusCode() == 200){
                outputStream.write(httpResponse.getFileContent());
            }

            outputStream.flush();
            outputStream.close();

        }
        catch(IOException ex){
            throw new InternalServerErrorException();
        }

    }

    /**
     * This method process the script file.
     * @return Nothing.
     * @exception IOException on executing process.
     */
    public void executeScript(HttpRequest httpRequest, HttpResource httpResource) throws IOException {

        ProcessBuilder processBuilder = new ProcessBuilder(httpResource.getFile().getAbsolutePath());

        Map<String, String> env = processBuilder.environment();
        env.clear();
        env.put("SERVER_PROTOCOL", httpRequest.getHttpVersion());
        //env.put("QUERY_STRING", httpRequest.getHttpBody().toString());

        httpRequest.getHttpHeaders().forEach((key, value) -> env.put("HTTP_" + key.toUpperCase(), value));

        Process process = processBuilder.start();
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = process.getInputStream().read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }

        fileContent = result.toByteArray();

    }


}
package com.project.webserver.server;

import com.project.webserver.exception.HttpException;
import com.project.webserver.exception.InternalServerErrorException;
import com.project.webserver.util.*;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final MimeTypesConfUtil mimeTypesConfUtil;
    private final HttpdConfUtil httpdConfUtil;
    private final Socket clientSocket;
    private final LoggerUtil logger;

    public ClientHandler(Socket clientSocket, MimeTypesConfUtil mimeTypes, HttpdConfUtil httpdConf, LoggerUtil logger) {
        this.clientSocket = clientSocket;
        this.httpdConfUtil = httpdConf;
        this.mimeTypesConfUtil = mimeTypes;
        this.logger = logger;
    }

    @Override
    public void run() {
        try {

            HttpResponse httpResponse = null;
            boolean isRequestProcessed = false;
            boolean isAccessCheckProcessed = false;

            HttpRequest httpRequest = new HttpRequest(clientSocket, httpdConfUtil);
            HttpResource httpResource = new HttpResource(httpdConfUtil);
            AccessCheckUtil accessCheckUtil = new AccessCheckUtil();
            HttpResponseUtil httpResponseUtil = new HttpResponseUtil(mimeTypesConfUtil);

            try{
                httpRequest.parseRequest();
                if(null != httpRequest.getHttpUri()) isRequestProcessed = true;
            }
            catch (HttpException ex) {
                httpResponse = httpResponseUtil.getResponse(ex);
            }

            if(isRequestProcessed){
                try{
                    httpResource.loadAccessFile(httpRequest.getHttpUri());
                    accessCheckUtil.checkAccess(httpRequest, httpResource);
                    isAccessCheckProcessed = true;
                }
                catch (HttpException ex) {
                    httpResponse = httpResponseUtil.getResponse(accessCheckUtil.getHtaccessConfUtil(), ex);
                }
            }

            if(isRequestProcessed && isAccessCheckProcessed){
                httpResponse = httpResponseUtil.getResponse(httpRequest, httpResource);
            }

            if(null == httpResponse){
                httpResponse = httpResponseUtil.getResponse(new InternalServerErrorException());
            }
            httpResponseUtil.sendResponse(clientSocket.getOutputStream(), httpResponse);
            logger.log(httpRequest, httpResponse);

        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally{
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
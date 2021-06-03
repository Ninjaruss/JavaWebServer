package com.project.webserver.server;

import com.project.webserver.exception.HttpException;
import com.project.webserver.exception.InternalServerErrorException;
import com.project.webserver.util.HttpdConfUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * The class implement utilities to process HTTP Request.
 * @author  Harsh Saxena
 * @version 1.0
 * @since   2020-09-08
 */
public class HttpRequest {

    private HttpdConfUtil httpdConfUtil;
    private Socket clientSocket;
    private String httpUri;
    private String httpVersion;
    private boolean containsScript;
    private boolean containsAlias;

    public Socket getClientSocket() {
        return clientSocket;
    }

    public String getHttpUri() {
        return httpUri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getHttpVerb() {
        return httpVerb;
    }

    public boolean isScriptAliased() {
        return containsScript;
    }

    public HashMap<String, String> getHttpHeaders() {
        return httpHeaders;
    }

    private String httpVerb;
    private byte[] httpBody;
    private HashMap<String, String> httpHeaders;


    public HttpRequest(Socket clientSocket, HttpdConfUtil httpdConf) {
        this.clientSocket = clientSocket;
        this.httpdConfUtil = httpdConf;
    }


    /**
     * This method parse the http request.
     * @return Nothing.
     */
    public void parseRequest() throws HttpException {

        try{
            String line;
            int lineCount = 0;
            InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
            BufferedReader bufferReader = new BufferedReader(inputStreamReader);
            while ((line = bufferReader.readLine()) != null && (line.length() != 0)) {
                lineCount += 1;
                if (lineCount == 1) {
                    String[] request = line.split("\\s+");
                    httpVerb = request[0];
                    httpUri = request[1];
                    if(httpUri.equals("/")) {
                        if(httpdConfUtil.getDirectoryIndex().isEmpty()){
                            httpUri = httpdConfUtil.getDocumentRoot() + "index.html";
                        }
                        else{
                            httpUri = httpdConfUtil.getDocumentRoot() + httpdConfUtil.getDirectoryIndex().get(0);
                        }
                    }
                    else{
                        StringTokenizer tokens = new StringTokenizer(httpUri, "/" );
                        String temporaryPath = "/";
                        boolean isDirectory = httpUri.endsWith( "/" );
                        while( tokens.hasMoreTokens() ) {
                            temporaryPath += tokens.nextToken();

                            if( tokens.hasMoreTokens() || isDirectory ) {
                                temporaryPath += "/" ;
                            }
                            if(httpdConfUtil.getAlias().containsKey(temporaryPath)) {
                                httpUri = httpdConfUtil.getAlias().get(temporaryPath) +
                                        remainingPath( tokens, isDirectory );
                                if(httpUri.endsWith("/")){
                                    httpUri += "index.html";
                                }
                                this.containsAlias = true;
                                break;
                            }
                            if(httpdConfUtil.getScriptAlias().containsKey(temporaryPath)) {
                                httpUri = httpdConfUtil.getScriptAlias().get(temporaryPath) +
                                        remainingPath( tokens, isDirectory );
                                this.containsScript = true;
                                break;
                            }
                        }
                        if(!containsScript && !containsAlias){
                            httpUri = httpdConfUtil.getDocumentRoot() + httpUri;
                            if(httpUri.endsWith("/")){
                                httpUri += "index.html";
                            }
                        }
                    }

                    httpVersion = request[2];
                } else {
                    String[] header = line.split(": ");
                    if (null == httpHeaders) {
                        httpHeaders = new HashMap();
                    }
                    httpHeaders.put(header[0], header[1]);
                }
            }

        }
        catch(Exception ex){
            throw new InternalServerErrorException();
        }
    }

    /**
     * This method is a utility method for getting file path
     * @return String file path.
     */
    private String remainingPath(StringTokenizer tokens, boolean trailingSlash) {
        String remainder = "";

        while( tokens.hasMoreTokens() ) {
            remainder += tokens.nextToken();

            if( tokens.hasMoreTokens() || trailingSlash ) {
                remainder += "/";
            }
        }

        return remainder;
    }

}
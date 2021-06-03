package com.project.webserver.util;

import com.project.webserver.exception.ForbiddenException;
import com.project.webserver.exception.HttpException;
import com.project.webserver.exception.InternalServerErrorException;
import com.project.webserver.exception.UnauthorizedException;
import com.project.webserver.server.HttpRequest;
import com.project.webserver.server.HttpResource;

import java.io.IOException;

/**
 * The class implement utilities for access checking .
 * @author  Harsh Saxena
 * @version 1.0
 * @since   2020-09-18
 */
public class AccessCheckUtil {

    private HtaccessConfUtil htaccessConfUtil;

    private final static String AUTHORIZATION_HEADER = "Authorization";

    public HtaccessConfUtil getHtaccessConfUtil() {
        return htaccessConfUtil;
    }

    /**
     * This method check the access rights.
     * @return Nothing.
     */
    public void checkAccess(HttpRequest httpRequest, HttpResource httpResource) throws HttpException {

        if(httpResource.isHtaccessExists()) {
            htaccessConfUtil = new HtaccessConfUtil();
            try{
                htaccessConfUtil.load(httpResource.getAccessFile());
            }
            catch(IOException exception){
                throw new InternalServerErrorException();
            }
            if (httpRequest.getHttpHeaders().containsKey(AUTHORIZATION_HEADER)) {
                String authInfo = httpRequest.getHttpHeaders().get(AUTHORIZATION_HEADER);
                HtpasswdConfUtil htpasswdConfUtil = htaccessConfUtil.getHtpasswdConfUtil();
                if (!htpasswdConfUtil.isAuthorized(authInfo)) {
                    throw new ForbiddenException();
                }
            } else {
                throw new UnauthorizedException();
            }
        }

    }

}

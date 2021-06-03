package com.project.webserver.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * The class implement utilities for HT Access file .
 * @author  Harsh Saxena
 * @version 1.0
 * @since   2020-09-18
 */
public class HtaccessConfUtil {

    private HashMap<String,String> directives;

    public HtpasswdConfUtil getHtpasswdConfUtil() {
        return htpasswdConfUtil;
    }

    public String getAuthType() {
        return directives.get("AuthType");
    }

    public String getAuthName() {
        return directives.get("AuthName");
    }


    private HtpasswdConfUtil htpasswdConfUtil;

    public HtaccessConfUtil(){
        directives = new HashMap<>();
    }

    /**
     * This method loads the htaccess file.
     * @return Nothing.
     */
    public void load(File accessFile) throws IOException {
        Scanner scanner = new Scanner(accessFile);
        while (scanner.hasNextLine()) {
            String[] line = scanner.nextLine().split("\\s+");
            String key = line[0];
            String value = line[1].replace("\"", "");

            switch(key) {
                case "AuthUserFile":
                    directives.put(key, value);
                    htpasswdConfUtil = new HtpasswdConfUtil(value);
                    break;
                case "AuthType":
                    directives.put(key, value);
                    break;
                case "AuthName":
                    directives.put(key, value);
                    break;
                case "Require":
                    directives.put(key, value);
            }

        }
    }

}

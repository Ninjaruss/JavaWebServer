package com.project.webserver.util;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

/**
 * The class implement utilities to process MIME Types Configuration.
 * @author  Harsh Saxena
 * @version 1.0
 * @since   2020-09-08
 */
public class MimeTypesConfUtil{

    private HashMap<String, String> mimeTypesMap;
    private String mimeType;

    /**
     * This method loads configuration.
     * @args  fileName Configuration filename.
     * @return Nothing.
     */
    public void load(String fileName) {

        mimeTypesMap = new HashMap<>();

        try{
            Scanner scanner = new Scanner(new File(fileName));
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split("\\s+");
                String key = line[0];
                String value = line[1];
                mimeTypesMap.put(key, value);
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

    }

    /**
     * This method return MIME Type.
     * @args  extension.
     * @return String MIME Type.
     */
    public String getMimeType(String extension) {
        mimeType = mimeTypesMap.get(extension);
        if(null == mimeType) {
            mimeType = "text/text";
        }
        return mimeType;
    }


}
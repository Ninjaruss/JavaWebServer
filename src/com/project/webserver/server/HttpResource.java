package com.project.webserver.server;

import com.project.webserver.util.HttpdConfUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * The class implement utilities to process HTTP Resource.
 * @author  Harsh Saxena
 * @version 1.0
 * @since   2020-09-08
 */
public class HttpResource {

    private File file;
    private HttpdConfUtil httpdConfUtil;
    private File accessFile;
    public File getFile() {
        return file;
    }

    public String getLastModified() {
        Date lastModified = new Date(file.lastModified());
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        String formattedDateString = dateFormat.format(lastModified);
        return formattedDateString;
    }

    public File getAccessFile() {
        return accessFile;
    }

    public HttpResource(HttpdConfUtil httpdConfUtil) {
        this.httpdConfUtil = httpdConfUtil;
    }

    /**
     * This method check whether file exist.
     * @return boolean This return whether file exist or not.
     */
    public boolean isExist() {
        return file.exists();
    }

    /**
     * This method creates file.
     * @return boolean.
     */
    public boolean createFile() {
        try{
            return file.createNewFile();
        }
        catch(IOException ex){
            return false;
        }

    }

    /**
     * This method deletes file.
     * @return boolean.
     */
    public boolean deleteFile() {
        return file.delete();
    }

    /**
     * Resolve the HT Access file path.
     * @return Nothing.
     */
    public void loadAccessFile(String uri) {

        file = new File(uri);
        Path tempPath = Paths.get(file.getParent());
        accessFile = tempPath.resolve(httpdConfUtil.getAccessFile()).toFile();
    }

    /**
     * This method check whether ht access file exist.
     * @return boolean This return whether file exist or not.
     */
    public boolean isHtaccessExists(){
        return accessFile.exists();
    }


}
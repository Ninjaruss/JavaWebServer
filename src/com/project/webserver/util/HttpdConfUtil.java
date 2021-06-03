package com.project.webserver.util;

import java.io.File;
import java.util.*;

/**
 * The class implement utilities to process Httpd Configuration.
 * @author  Harsh Saxena
 * @version 1.0
 * @since   2020-09-08
 */
public class HttpdConfUtil{

    private int port;
    private String documentRoot;
    private String logFile;
    private String serverRoot;
    private String accessFile;
    private ArrayList<String> directoryIndex;
    private HashMap<String,String> scriptAlias;
    private HashMap<String,String> alias;

    public HttpdConfUtil(){
        this.scriptAlias = new HashMap<>();
        this.alias = new HashMap<>();
    }

    public int getPort() {
        return port;
    }

    public String getDocumentRoot() {
        return documentRoot;
    }

    public String getAccessFile() {
        return accessFile;
    }

    public String getLogFile() {
        return logFile;
    }

    public List<String> getDirectoryIndex() {
        return directoryIndex;
    }

    public Map<String,String> getScriptAlias() {
        return scriptAlias;
    }

    public Map<String,String> getAlias() {
        return alias;
    }

    /**
     * This method loads configuration.
     * @args  fileName Configuration filename.
     * @return Nothing.
     */
    public void load(String fileName) {
        try{
            Scanner scanner = new Scanner(new File(fileName));
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split("\\s+");
                String key = line[0];
                String value = line[1].replace("\"", "");
                switch (key) {
                    case "Listen":
                        port = Integer.parseInt(value);
                        break;
                    case "DocumentRoot":
                        documentRoot = value;
                        break;
                    case "LogFile":
                        logFile = value;
                        break;
                    case "Alias":
                        alias.put(value,line[2].replace("\"", ""));
                        break;
                    case "ScriptAlias":
                        scriptAlias.put(value,line[2].replace("\"", ""));
                        break;
                    case "AccessFile":
                        accessFile = value;
                        break;
                    case "DirectoryIndex":
                        directoryIndex = new ArrayList<>(Arrays.asList(value.split("\\s+")));
                        break;
                    default:
                        break;
                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

    }

}
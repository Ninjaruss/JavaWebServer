package com.project.webserver.util;

import com.project.webserver.server.HttpRequest;
import com.project.webserver.server.HttpResponse;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * The class implement logging utilities.
 * @author  Harsh Saxena
 * @version 1.0
 * @since   2020-09-08
 */
public class LoggerUtil {

    private PrintWriter printWriter;

    /**
     * This method initialize Logger Util.
     * @args  logFilePath Path of log file.
     * @return Nothing.
     */
    public void initialize(String logFilePath) throws IOException {
        FileWriter fileWriter = new FileWriter(logFilePath, true);
        printWriter = new PrintWriter(fileWriter);
    }


    /**
     * This method write logs.
     * @args  httpRequest HTTP Request.
     * @args  httpResponse HTTP Response.
     * @return Nothing.
     */
    public void log(HttpRequest httpRequest, HttpResponse httpResponse){

        String log = String.format("%s %s [%s] %s %s %s\n", httpRequest.getClientSocket().getInetAddress(),
                "-",
                getDateTime(ZonedDateTime.now()),
                httpRequest.getHttpVerb(),
                httpRequest.getHttpVersion(),
                httpResponse.getStatusCode());

        System.out.printf(log);
        printWriter.printf(log);
        printWriter.close();

    }

    private String getDateTime(ZonedDateTime timeStamp) {
        return String.format("%02d/%s/%04d:%02d:%02d:%02d %s",
                timeStamp.getDayOfMonth(),
                timeStamp.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                timeStamp.getYear(), timeStamp.getHour(), timeStamp.getMinute(),
                timeStamp.getSecond(), timeStamp.getOffset());
    }

}
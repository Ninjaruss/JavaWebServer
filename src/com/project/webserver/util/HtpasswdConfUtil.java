package com.project.webserver.util;

import com.project.webserver.exception.HttpException;
import com.project.webserver.exception.InternalServerErrorException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Scanner;

/**
 * The class implements authentication with htaccess password.
 * @author  Russell Azucenas
 * @version 1.0
 * @since   9/10/2020
 */
public class HtpasswdConfUtil {
    private HashMap<String, String> passwords;

    public HtpasswdConfUtil(String filename ) throws IOException {
        //System.out.println( "Password file: " + filename );

        this.passwords = new HashMap<String, String>();
        this.load(filename);
    }

    // Loads the .htpasswd file data
    public void load(String filename){
        try{
            Scanner scanner = new Scanner(new File(filename));
            while (scanner.hasNextLine()) {
                parseLine(scanner.nextLine());
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * This method parses a htpasswd string and adds it to HashMap passwords.
     * @args  String line.
     * @return Nothing.
     */
    protected void parseLine( String line ) {
        String[] tokens = line.split( ":" );

        if( tokens.length == 2 ) {
            passwords.put( tokens[ 0 ], tokens[ 1 ].replace( "{SHA}", "" ).trim() );
        }
    }

    /**
     * This method authenticates the client with the given credentials.
     * @args  String authInfo. (Base64 encoded string "username:password")
     * @return Boolean.
     */
    public boolean isAuthorized( String _authInfo ) throws HttpException {
        // authInfo is provided in the header received from the client
        // as a Base64 encoded string.
        try{
            String[] authInfoSplit = _authInfo.split("\\s+");
            String authInfo = authInfoSplit[1];
            String credentials = new String(
                    Base64.getDecoder().decode( authInfo ),
                    Charset.forName( "UTF-8" )
            );

            // The string is the key:value pair username:password
            String[] tokens = credentials.split( ":" );
            return verifyPassword(tokens[0], tokens[1]);
        }
        catch(Exception ex){
            throw new InternalServerErrorException();
        }

    }

    /**
     * This method verifies if the username and password are valid.
     * @args  String username, String password
     * @return Boolean.
     */
    private boolean verifyPassword( String username, String password ) throws HttpException {
        // encrypt the password, and compare it to the password stored
        // in the password file (keyed by username)
        String encryptedPassword = encryptClearPassword(password);

        if (passwords.containsKey(username)){
            if (passwords.get(username).equals(encryptedPassword)){
                return true;
            }
        }
        return false;
    }

    /**
     * This method encrypts a cleartext password with SHA-1 encryption algorithm.
     * @args  String password
     * @return String. (Base64 encoded)
     */
    private String encryptClearPassword( String password ) throws HttpException {
        // Encrypt the cleartext password (that was decoded from the Base64 String
        // provided by the client) using the SHA-1 encryption algorithm
        try {
            MessageDigest mDigest = MessageDigest.getInstance( "SHA-1" );
            byte[] result = mDigest.digest( password.getBytes() );

            return Base64.getEncoder().encodeToString( result );
        } catch( Exception e ) {
            throw new InternalServerErrorException();
        }
    }
}
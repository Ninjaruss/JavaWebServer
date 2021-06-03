import com.project.webserver.server.ClientHandler;
import com.project.webserver.util.HtpasswdConfUtil;
import com.project.webserver.util.HttpdConfUtil;
import com.project.webserver.util.LoggerUtil;
import com.project.webserver.util.MimeTypesConfUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The class implement Server utilities.
 * @author  Harsh Saxena
 * @version 1.0
 * @since   2020-09-08
 */
public class Server {

    private HttpdConfUtil httpdConfUtil;
    private MimeTypesConfUtil mimeTypesUtil;
    private HtpasswdConfUtil htpasswdConfUtil;

    /**
     * This method loads configuration.
     * @args  httpdConfFileName httpd conf.
     * @args  mimeTypeFileName mime type conf.
     * @args  htpasswdFileName htpasswd type conf.
     * @return Nothing.
     */
    public void loadConf(String httpdConfFileName, String mimeTypeFileName){
        try{
            httpdConfUtil = new HttpdConfUtil();
            httpdConfUtil.load(httpdConfFileName);

            mimeTypesUtil = new MimeTypesConfUtil();
            mimeTypesUtil.load(mimeTypeFileName);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method starts the server.
     * @return Nothing.
     */
    public void start() {

        ServerSocket serverSocket = null;

        try{
            serverSocket = new ServerSocket(httpdConfUtil.getPort());
            System.out.println("Server running on port: " + httpdConfUtil.getPort());
            while (true) {
                Socket clientSocket = serverSocket.accept();
                LoggerUtil logger = new LoggerUtil();
                logger.initialize(httpdConfUtil.getLogFile());
                ClientHandler clientHandler = new ClientHandler(clientSocket, mimeTypesUtil, httpdConfUtil, logger);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        finally{
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public static void main(String[] args) {

        Server server = new Server();

        try{
            String httpdConfFileName = args[0];
            String mimeTypeFileName = "../conf/mime.types";

            server.loadConf(httpdConfFileName, mimeTypeFileName);

            server.start();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }


    }

}

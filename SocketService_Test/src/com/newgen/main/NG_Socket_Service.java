package com.newgen.main;

import com.newgen.process.doComms;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class NG_Socket_Service {

    public static String err;
    private static final Logger logger = Logger.getLogger("consoleLogger");
    static {
        PropertyConfigurator.configure(System.getProperty("user.dir") + File.separator + "Config" + File.separator + "log4j_WebServiceWrapper.properties");
    }

    public static void main(String[] args) {
        int port = 4445;
        int k = 0;
        ServerSocket listener = null;
        Socket server = null;

        //******* Establish connection *************
        try {
            listener = new ServerSocket(port);


            while (true) {
                k = k + 1;
                logger.info("Waiting for Request Count--" + k);
                System.out.println("Waiting for Request Count--" + k);
                server = listener.accept();
                doComms conn_c = new doComms(server);
                Thread t = new Thread(conn_c);
                t.start();
            }


        } catch (IOException ioe) {
            logger.info("IOException on socket listen:11111111 " + ioe);
            System.out.println("Catch  3 IOException on socket listen:11111111 " + ioe);
            ioe.printStackTrace();
        } finally {
            try {
                if (listener != null) {
                    listener.close();
                    System.out.println("Closing Listener");
                    logger.info("Closing Listener");
                }
                if (server != null) {
                    server.close();
                    System.out.println("Closing Server Socket");
                    logger.info("Closing Server Socket");
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("Exception " + e);
                System.out.println("Exception " + e);
            }
        }
    }
}

//----------------------------------------------------------------------------------------------------------------
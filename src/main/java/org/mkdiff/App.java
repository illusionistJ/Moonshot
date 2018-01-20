package org.mkdiff;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mkdiff.server.EchoServer;


public class App
{
    private static Logger LOGGER = LogManager.getLogger(App.class);

    public static void main( String[] args ) throws Exception
    {
        if(args.length != 1){
            LOGGER.error("Usage: {} <port>", EchoServer.class.getSimpleName() );
        }
        int port = Integer.parseInt(args[0]);
        LOGGER.info("[Moonshot] Server is started.");
        new EchoServer(port).start();
        LOGGER.info("[Moonshot] Server is stopped.");
    }
}

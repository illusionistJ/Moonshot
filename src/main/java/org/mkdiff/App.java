package org.mkdiff;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mkdiff.client.EchoClient;
import org.mkdiff.server.EchoServer;


public class App
{
    private static Logger LOGGER = LogManager.getLogger(App.class);
    private Options options;

    public App(){
        options = new Options();
        options.addOption(Option.builder("m").required().hasArg().longOpt("mode").desc("choose Moonshot Launch Mode").build());
        options.addOption(Option.builder("p").required().hasArg().longOpt("port").build());
        options.addOption(Option.builder("h").hasArg().longOpt("host").build());
    }

    public Options getOptions(){
        return options;
    }

    public static void main( String[] args ) throws Exception
    {
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run()
            {
                LOGGER.info("[Moonshot] Server is stopped.");
            }
        });

        App app=new App();

        try{
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(app.getOptions(), args);
            int port;

            switch (cmd.getOptionValue("mode")){
                case "EchoServer":
                    port = Integer.parseInt(cmd.getOptionValue("port"));
                    LOGGER.info("[Moonshot] Echo Server is started.");
                    new EchoServer(port).start();
                    break;
                case "EchoClient":
                    port = Integer.parseInt(cmd.getOptionValue("port"));
                    String host = cmd.getOptionValue("host");
                    if (host != null){
                        LOGGER.info("[Moonshot] Echo Client is started.");
                        new EchoClient(host, port).start();
                        LOGGER.info("[Moonshot] Echo Client is stopped.");
                    }else{
                        LOGGER.error("should insert host address with option '-h' or '--host'" );
                    }
                    break;
                default:

                    break;
            }

        }catch( ParseException exp ) {
            LOGGER.error("Argument Exceptions : {}", exp.getMessage() );
        }
    }
}

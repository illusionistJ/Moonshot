package org.mkdiff;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mkdiff.server.HttpServer;


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
        App app=new App();

        try{
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(app.getOptions(), args);

            Runtime.getRuntime().addShutdownHook(new Thread()
            {
                public void run()
                {
                    LOGGER.info("[Moonshot] Server is stopped.");
                }
            });

            int port;

            switch (cmd.getOptionValue("mode")){
                case "Server":
                    port = Integer.parseInt(cmd.getOptionValue("port"));
                    LOGGER.info("[Moonshot]  Server will be started.");
                    new HttpServer(port).start();
                    break;
                default:
                    break;
            }

        }catch( ParseException exp ) {
            LOGGER.error("Argument Exceptions : {}", exp.getMessage() );
        }
    }
}

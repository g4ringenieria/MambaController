
package com.neogroup.controller;

import com.neogroup.controller.processors.ConnectionsProcessor;
import com.neogroup.controller.processors.DeviceProcessor;
import com.neogroup.controller.processors.TT8750DeviceProcessor;
import com.neogroup.controller.processors.GeneralProcessor;
import com.neogroup.controller.processors.Processor;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Application 
{
    private static final Application instance = new Application ();
    private ConnectionManager connection; 
    private ConsoleManager console;
    private Logger logger;
    private List<Processor> processors;
    
    public static Application getInstance ()
    {
        return instance;
    }
    
    public static void main(final String[] args)
    {
        try
        {
            int port = Integer.parseInt(args[0]);   
            String modelType = args[1];
            getInstance().getConnectionManager().setPort(port);
            getInstance().addProcessor(new GeneralProcessor());
            getInstance().addProcessor(new ConnectionsProcessor());
            switch (modelType)
            {
                case "TT8750":
                    getInstance().addProcessor(new TT8750DeviceProcessor());
                    break;
                default:
                    throw new Exception ("ModelType \"" + modelType + "\" not found !!");
            }
            getInstance().start();  
        }
        catch (Exception ex)
        {
            System.out.println ("Error: " + ex.getMessage() + "\nUSAGE: java -jar NeoGroupController.jar PORT MODELTYPE");
            System.exit(1);
        }
    }
    
    private Application ()
    {
        connection = new ConnectionManager();
        console = new ConsoleManager();
        processors = new ArrayList<Processor>();
    }
    
    public void destroy ()
    {
        stop();
        if (processors != null)
        {
            for (Processor handler : processors)
                try { handler.stop(); } catch (Exception ex) {}
            processors.clear();
            processors = null;
        }
        if (console != null)
        {
            try { console.stop(); } catch (Exception ex) {}
            console = null;
        }
        if (connection != null)
        {
            try { connection.stop(); } catch (Exception ex) {}
            connection = null;
        }
        System.exit(0);
    }
    
    public void start ()
    {
        getLogger().info("Initializing Controller ...");
        for (Processor processor : processors)
            processor.start();
        console.start();
        connection.start();
        getLogger().info("Controller initialized !!");
    }
    
    public void stop ()
    {
        getLogger().info("Finalizing Controller ...");
        console.stop();
        connection.stop();
        for (Processor processor : processors)
            processor.stop();
        getLogger().info("Controller finalized !!");
    }
    
    public ConnectionManager getConnectionManager ()
    {
        return connection;
    }
    
    public ConsoleManager getConsoleManager ()
    {
        return console;
    }
    
    public void addProcessor (Processor processor)
    {
        this.processors.add (processor);
    }
    
    public List<Processor> getProcessors ()
    {
        return processors;
    }
    
    public Logger getLogger ()
    {   
        if (logger == null)
        {
            logger = Logger.getLogger(this.getClass().getName());
            try
            {   
                FileHandler handler = new FileHandler("log.txt", 1024000, 1, true);
                handler.setFormatter(new SimpleFormatter());
                logger.setUseParentHandlers(false);
                logger.addHandler(handler);
            } 
            catch (Exception e){e.printStackTrace();}
        }
        return logger;
    }
}
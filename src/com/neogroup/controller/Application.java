
package com.neogroup.controller;

import com.neogroup.controller.processors.ConnectionsProcessor;
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
    private ConnectionManager connectionManager; 
    private ConsoleManager consoleManager;
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
            modelType = Character.toString(modelType.charAt(0)).toUpperCase() + modelType.substring(1);
            getInstance().getConnectionManager().setPort(port);
            getInstance().addProcessor(new GeneralProcessor());
            getInstance().addProcessor(new ConnectionsProcessor());
            getInstance().addProcessor((Processor)Class.forName("com.neogroup.controller.processors." + modelType + "DeviceProcessor").newInstance());
            getInstance().start();  
        }
        catch (Exception ex)
        {
            System.out.println ("Error: " + ex.toString() + "\nUSAGE: java -jar NeoGroupController.jar PORT MODELTYPE");
            System.exit(1);
        }
    }
    
    private Application ()
    {
        connectionManager = new ConnectionManager();
        consoleManager = new ConsoleManager();
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
        if (consoleManager != null)
        {
            try { consoleManager.stop(); } catch (Exception ex) {}
            consoleManager = null;
        }
        if (connectionManager != null)
        {
            try { connectionManager.stop(); } catch (Exception ex) {}
            connectionManager = null;
        }
        System.exit(0);
    }
    
    public void start ()
    {
        getLogger().info("Initializing Controller ...");
        for (Processor processor : processors)
            processor.start();
        consoleManager.start();
        connectionManager.start();
        getLogger().info("Controller initialized !!");
    }
    
    public void stop ()
    {
        getLogger().info("Finalizing Controller ...");
        consoleManager.stop();
        connectionManager.stop();
        for (Processor processor : processors)
            processor.stop();
        getLogger().info("Controller finalized !!");
    }
    
    public ConnectionManager getConnectionManager ()
    {
        return connectionManager;
    }
    
    public ConsoleManager getConsoleManager ()
    {
        return consoleManager;
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

package com.neogroup.controller;

import com.neogroup.controller.processors.ConnectionsProcessor;
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
    private int type;
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
            int type = Integer.parseInt(args[0]);
            int port = Integer.parseInt(args[1]);
            getInstance().setType(type);
            getInstance().getConnectionManager().setPort(port);
            getInstance().start();   
        }
        catch (Exception ex)
        {
            System.out.println ("Usage: java -jar NeoGroupController.jar [TYPE] [PORT]");
        }
    }
    
    private Application ()
    {
        connection = new ConnectionManager();
        console = new ConsoleManager();
        processors = new ArrayList<Processor>();
        processors.add(new GeneralProcessor());
        processors.add(new ConnectionsProcessor());
        processors.add(new TT8750DeviceProcessor());
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
    
    public Logger getLogger ()
    {   
        if (logger == null)
        {
            logger = Logger.getLogger(this.getClass().getName());
            try
            {   
                FileHandler handler = new FileHandler("log_" + getType() + ".txt", 1024000, 1, true);
                handler.setFormatter(new SimpleFormatter());
                logger.setUseParentHandlers(false);
                logger.addHandler(handler);
            } 
            catch (Exception e){e.printStackTrace();}
        }
        return logger;
    }

    public int getType () 
    {
        return type;
    }

    public void setType (int type) 
    {
        this.type = type;
    }
}
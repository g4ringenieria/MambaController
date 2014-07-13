
package com.neogroup.controller;

import com.neogroup.controller.processors.TT8750DeviceProcessor;
import com.neogroup.controller.processors.GeneralProcessor;
import com.neogroup.controller.processors.Processor;
import java.io.File;
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
            getInstance().getConnection().setPort(port);
            getInstance().start();   
        }
        catch (Exception ex)
        {
            System.out.println ("Usage: java -jar NeoGroupController.jar [PORT]");
        }
    }
    
    private Application ()
    {
        logger = createLogger();
        connection = new ConnectionManager();
        console = new ConsoleManager();
        processors = new ArrayList<Processor>();
        processors.add(new GeneralProcessor());
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
        for (Processor handler : processors)
            handler.start();
        console.start();
        connection.start();
        getLogger().info("Controller initialized !!");
    }
    
    public void stop ()
    {
        getLogger().info("Finalizing Controller ...");
        console.stop();
        connection.stop();
        for (Processor handler : processors)
            handler.stop();
        getLogger().info("Controller finalized !!");
    }
    
    private Logger createLogger ()
    {
        Logger logger = Logger.getLogger(this.getClass().getName());
        try
        {    
            File dir = new File("./logs");
            dir.mkdir();
            FileHandler handler = new FileHandler(dir.getPath() + File.separatorChar + "controllerLog.txt", 1024000, 1, true);
            handler.setFormatter(new SimpleFormatter());
            logger.setUseParentHandlers(false);
            logger.addHandler(handler);
        } 
        catch (Exception e){e.printStackTrace();}
        return logger;
    }
    
    public ConnectionManager getConnection ()
    {
        return connection;
    }
    
    public ConsoleManager getConsole ()
    {
        return console;
    }
    
    public Logger getLogger ()
    {   
        return logger;
    }
    
}
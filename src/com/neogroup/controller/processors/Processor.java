
package com.neogroup.controller.processors;

import com.neogroup.controller.Application;
import com.neogroup.controller.ConnectionManager;
import com.neogroup.controller.ConsoleManager;
import java.util.logging.Logger;

public abstract class Processor
{
    private boolean running;
    
    public Processor ()
    {
        this.running = false;
    }

    protected Application getApplication ()
    {
        return Application.getInstance();
    }
    
    protected Logger getLogger()
    {
        return getApplication().getLogger();
    }
    
    protected ConnectionManager getConnectionManager()
    {
        return getApplication().getConnectionManager();
    }
    
    protected ConsoleManager getConsoleManager()
    {
        return getApplication().getConsoleManager();
    }
    
    public final void start ()
    {
        if (!running)
        {
            running = true;
            onStarted();
            Application.getInstance().getLogger().info("Processor \"" + this.getClass().getName() + "\" started !!");
        }
    }
    
    public final void stop ()
    {
        if (running)
        {
            running = false;
            onStopped();
            Application.getInstance().getLogger().info("Processor \"" + this.getClass().getName() + "\" stopped !!");
        }
    }
    
    public abstract void onStarted();
    public abstract void onStopped();
}

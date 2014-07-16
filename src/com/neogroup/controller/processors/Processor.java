
package com.neogroup.controller.processors;

import com.neogroup.controller.Application;

public abstract class Processor
{
    private boolean running;
    
    public Processor ()
    {
        this.running = false;
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

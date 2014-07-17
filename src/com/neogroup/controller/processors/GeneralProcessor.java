
package com.neogroup.controller.processors;

import com.neogroup.controller.ConsoleManager;
import java.io.PrintStream;
import java.util.List;

public class GeneralProcessor extends Processor implements ConsoleManager.ConsoleListener
{
    @Override
    public void onStarted()
    {
        getConsoleManager().addConsoleListener(this);
    }

    @Override
    public void onStopped()
    {
        getConsoleManager().removeConsoleListener(this);
    }

    @Override
    public void onCommandEntered(String command, List<String> commandArguments, PrintStream out)
    {
        if (command.equals("exit"))
        {
            getApplication().destroy();
        }
        else if (command.equals("status")) 
        {
            out.println ("Controller model name: " + getApplication().getName());
        }
    }
}
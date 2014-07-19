
package com.neogroup.controller.processors;

import com.neogroup.controller.ConsoleManager;
import java.io.PrintStream;
import java.util.List;

public class ScriptsProcessor extends Processor implements ConsoleManager.ConsoleListener
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
        if (command.equals("setLocalScriptsDir"))
        {
            getScriptsManager().setLocalScriptsDir(commandArguments.get(0));
            out.println ("Commands local scripts dir modified successfully !!");;
        }
        else if (command.equals("setScriptName"))
        {
            getScriptsManager().setScriptName(commandArguments.get(0));
            out.println ("Commands script name modified successfully !!");;
        } 
        else if (command.equals("status")) 
        {
            out.println ("Scripts local dir: " + getScriptsManager().getLocalScriptsDir());
            out.println ("Scripts filename: " + getScriptsManager().getScriptName());
        }
    }
}

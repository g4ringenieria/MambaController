
package com.neogroup.controller.processors;

import com.neogroup.controller.ConsoleManager;
import java.io.PrintStream;
import java.util.List;

public class CommandsProcessor extends Processor implements ConsoleManager.ConsoleListener
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
            getCommandManager().setLocalScriptsDir(commandArguments.get(0));
            out.println ("Commands local scripts dir modified successfully !!");;
        }
        else if (command.equals("setScriptName"))
        {
            getCommandManager().setScriptName(commandArguments.get(0));
            out.println ("Commands script name modified successfully !!");;
        } 
        else if (command.equals("status")) 
        {
            out.println ("Scripts local dir: " + getCommandManager().getLocalScriptsDir());
            out.println ("Scripts filename: " + getCommandManager().getScriptName());
        }
    }
}

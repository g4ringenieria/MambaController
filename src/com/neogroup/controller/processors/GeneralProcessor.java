
package com.neogroup.controller.processors;

import com.neogroup.controller.Application;
import com.neogroup.controller.ConsoleManager;
import java.io.PrintStream;
import java.util.List;

public class GeneralProcessor extends Processor implements ConsoleManager.ConsoleListener
{
    @Override
    public void start()
    {
        Application.getInstance().getConsoleManager().addConsoleListener(this);
    }

    @Override
    public void stop()
    {
        Application.getInstance().getConsoleManager().removeConsoleListener(this);
    }

    @Override
    public void onCommandEntered(String command, List<String> commandArguments, PrintStream out)
    {
        if (command.equals("exit"))
        {
            Application.getInstance().destroy();
        }
    }
}
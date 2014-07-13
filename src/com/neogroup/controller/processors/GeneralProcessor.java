
package com.neogroup.controller.processors;

import com.neogroup.controller.Application;
import com.neogroup.controller.ConsoleManager;
import java.util.List;

public class GeneralProcessor extends Processor implements ConsoleManager.ConsoleListener
{
    @Override
    public void start()
    {
        Application.getInstance().getConsole().addConsoleListener(this);
    }

    @Override
    public void stop()
    {
        Application.getInstance().getConsole().removeConsoleListener(this);
    }

    @Override
    public void onCommandEntered(String command, List<String> commandTokens)
    {
        if (command.equals("exit"))
        {
            Application.getInstance().destroy();
        }
    }
}
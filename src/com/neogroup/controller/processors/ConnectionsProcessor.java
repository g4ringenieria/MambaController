
package com.neogroup.controller.processors;

import com.neogroup.controller.Application;
import com.neogroup.controller.ConsoleManager;
import java.util.List;

public class ConnectionsProcessor extends Processor implements ConsoleManager.ConsoleListener
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
        if (command.equals("enableDebug"))
        {
            Application.getInstance().getConnection().setLogging(true);
            System.out.println("Logging enabled !!");
        }
        else if (command.equals("disableDebug"))
        {
            Application.getInstance().getConnection().setLogging(false);
            System.out.println("Logging disabled !!");
        }
    }
}
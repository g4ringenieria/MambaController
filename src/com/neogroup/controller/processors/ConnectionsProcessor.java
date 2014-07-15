
package com.neogroup.controller.processors;

import com.neogroup.controller.Application;
import com.neogroup.controller.Connection;
import com.neogroup.controller.ConsoleManager;
import java.util.List;

public class ConnectionsProcessor extends Processor implements ConsoleManager.ConsoleListener
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
    public void onCommandEntered(String command, List<String> commandTokens)
    {
        if (command.equals("enableDebug"))
        {
            Application.getInstance().getConnectionManager().setLogging(true);
            System.out.println("Logging enabled !!");
        }
        else if (command.equals("disableDebug"))
        {
            Application.getInstance().getConnectionManager().setLogging(false);
            System.out.println("Logging disabled !!");
        }
        else if (command.equals("listConnections") || command.equals("lc"))
        {
            List<Connection> connections = Application.getInstance().getConnectionManager().getConnections();
            synchronized (connections)
            {
                System.out.println("Connection size: " + connections.size());
                for (Connection connection : connections)
                    System.out.println(connection.toString());
            }
        }
    }
}
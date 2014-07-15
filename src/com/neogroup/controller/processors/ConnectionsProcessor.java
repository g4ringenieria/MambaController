
package com.neogroup.controller.processors;

import com.neogroup.controller.Application;
import com.neogroup.controller.Connection;
import com.neogroup.controller.ConsoleManager;
import com.neogroup.utils.StringUtils;
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
    public void onCommandEntered(String command, List<String> commandArguments)
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
                System.out.println("Connections size: " + connections.size());
                for (Connection connection : connections)
                    System.out.println(connection.toString());
            }
        }
        else if (command.equals("sendConnection") || command.equals("sc"))
        {
            int connectionIdentifierIndex = commandArguments.indexOf("-c") + 1;
            int messageIndex = commandArguments.indexOf("-m") + 1;
            if (connectionIdentifierIndex == 0 || messageIndex == 0)
            {
                System.out.println ("USAGE: sendConnection [OPTIONS] -c 10064 -m \"hello world\"");
            }
            else
            {
                int connectionIdentifier = Integer.parseInt(commandArguments.get(connectionIdentifierIndex));
                String message = commandArguments.get(messageIndex);
                byte[] data = (commandArguments.indexOf("--hex") >= 0)? StringUtils.getByteArrayFromHexString(message) : message.getBytes();
                try
                {
                    Application.getInstance().getConnectionManager().sendToConnection(connectionIdentifier, data);
                    System.out.println ("Message sent succesfully !!");
                }
                catch (Exception ex)
                {
                    System.out.println ("Error sending the message: " + ex.getMessage());
                }
            }
        }
    }
}
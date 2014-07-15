
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
            int connectionIdentifierIndex = commandArguments.indexOf("-i") + 1;
            int connectionIdIndex = commandArguments.indexOf("-c") + 1;
            int messageIndex = commandArguments.indexOf("-m") + 1;
            if ((connectionIdentifierIndex == 0 && connectionIdIndex == 0) || messageIndex == 0)
            {
                System.out.println ("USAGE: sendConnection [OPTIONS] -i 10064 -m \"hello world\"");
            }
            else
            {
                try
                {
                    String message = commandArguments.get(messageIndex);
                    byte[] data = (commandArguments.indexOf("--hex") >= 0)? StringUtils.getByteArrayFromHexString(message) : message.getBytes();
                    if (connectionIdentifierIndex > 0)
                    {
                        int connectionIdentifier = Integer.parseInt(commandArguments.get(connectionIdentifierIndex));    
                        Application.getInstance().getConnectionManager().sendToConnectionIdentifier(connectionIdentifier, data);
                    }
                    else 
                    {
                        int connectionId = Integer.parseInt(commandArguments.get(connectionIdIndex));
                        Application.getInstance().getConnectionManager().sendToConnection(connectionId, data);
                    }
                    System.out.println ("Message sent succesfully !!");
                }
                catch (Exception ex)
                {
                    System.out.println ("Error sending the message: " + ex.getMessage());
                }
            }
        }
        else if (command.equals("closeConnection") || command.equals("cc"))
        {
            int connectionIdentifierIndex = commandArguments.indexOf("-i") + 1;
            int connectionIdIndex = commandArguments.indexOf("-c") + 1;
            if (connectionIdentifierIndex == 0 && connectionIdIndex == 0)
            {
                System.out.println ("USAGE: closeConnection -i 10064");
            }
            else
            {
                try
                {
                    if (connectionIdentifierIndex > 0)
                    {
                        int connectionIdentifier = Integer.parseInt(commandArguments.get(connectionIdentifierIndex));
                        Application.getInstance().getConnectionManager().closeConnectionIdentifier(connectionIdentifier);
                    }
                    else
                    {
                        int connectionId = Integer.parseInt(commandArguments.get(connectionIdIndex));
                        Application.getInstance().getConnectionManager().closeConnection(connectionId);
                    }
                    System.out.println ("Connection closed succesfully !!");
                }
                catch (Exception ex)
                {
                    System.out.println ("Error closing connection: " + ex.getMessage());
                }
            }
        }
    }
}
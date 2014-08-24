
package com.neogroup.controller.processors;

import com.neogroup.controller.Connection;
import com.neogroup.controller.ConsoleManager;
import com.neogroup.utils.StringUtils;
import java.io.PrintStream;
import java.util.List;

public class ConnectionsProcessor extends Processor implements ConsoleManager.ConsoleListener, Connection.ConnectionListener
{
    @Override
    public void onStarted()
    {
        getConsoleManager().addConsoleListener(this);
        getConnectionManager().addConnectionListener(this);
    }

    @Override
    public void onStopped()
    {
        getConsoleManager().removeConsoleListener(this);
        getConnectionManager().removeConnectionListener(this);
    }

    @Override
    public void onCommandEntered(String command, List<String> commandArguments, PrintStream out)
    {
        if (command.equals("enableDebug"))
        {
            getConnectionManager().setLogging(true);
            out.println("Logging enabled !!");
        }
        else if (command.equals("disableDebug"))
        {
            getConnectionManager().setLogging(false);
            out.println("Logging disabled !!");
        }
        else if (command.equals("listConnections") || command.equals("lc"))
        {
            List<Connection> connections = getConnectionManager().getConnections();
            synchronized (connections)
            {
                out.println("Connections size: " + connections.size());
                for (Connection connection : connections)
                    out.println(connection.toString());
            }
        }
        else if (command.equals("sendConnection") || command.equals("sc"))
        {
            int connectionIdentifierIndex = commandArguments.indexOf("-i") + 1;
            int connectionIdIndex = commandArguments.indexOf("-c") + 1;
            int messageIndex = commandArguments.indexOf("-m") + 1;
            if ((connectionIdentifierIndex == 0 && connectionIdIndex == 0) || messageIndex == 0)
            {
                out.println ("USAGE: sendConnection [OPTIONS] -i 10064 -m \"hello world\"");
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
                        getConnectionManager().sendToConnectionIdentifier(connectionIdentifier, data);
                    }
                    else 
                    {
                        int connectionId = Integer.parseInt(commandArguments.get(connectionIdIndex));
                        getConnectionManager().sendToConnection(connectionId, data);
                    }
                    out.println ("Message sent succesfully !!");
                }
                catch (Exception ex)
                {
                    out.println ("Error sending the message: " + ex.getMessage());
                }
            }
        }
        else if (command.equals("closeConnection") || command.equals("cc"))
        {
            int connectionIdentifierIndex = commandArguments.indexOf("-i") + 1;
            int connectionIdIndex = commandArguments.indexOf("-c") + 1;
            if (connectionIdentifierIndex == 0 && connectionIdIndex == 0)
            {
                out.println ("USAGE: closeConnection -i 10064");
            }
            else
            {
                try
                {
                    if (connectionIdentifierIndex > 0)
                    {
                        int connectionIdentifier = Integer.parseInt(commandArguments.get(connectionIdentifierIndex));
                        getConnectionManager().closeConnectionIdentifier(connectionIdentifier);
                    }
                    else
                    {
                        int connectionId = Integer.parseInt(commandArguments.get(connectionIdIndex));
                        getConnectionManager().closeConnection(connectionId);
                    }
                    out.println ("Connection closed succesfully !!");
                }
                catch (Exception ex)
                {
                    out.println ("Error closing connection: " + ex.getMessage());
                }
            }
        }
        else if (command.equals("setConnectionTimeout") || command.equals("scto"))
        {
            int connectionIdIndex = commandArguments.indexOf("-c") + 1;
            int timeoutIndex = commandArguments.indexOf("-t") + 1;
            try
            {
                int connectionId = Integer.parseInt(commandArguments.get(connectionIdIndex));
                getConnectionManager().getConnection(connectionId).setInactivityTimeout(Integer.parseInt(commandArguments.get(timeoutIndex)));
                out.println ("Connection timeout changed succesfully !!");
            }
            catch (Exception ex)
            {
                out.println ("Error changing connection timeout: " + ex.getMessage());
            }
        }
        else if (command.equals("status")) 
        {
            out.println ("Connections debug mode: " + (getConnectionManager().isLogging()?"true":"false"));
            out.println ("Connections port: " + getConnectionManager().getPort());
            out.println ("Connections: " + getConnectionManager().getConnections().size());
        }
    }

    @Override
    public void onConnectionDataReceived(Connection connection, byte[] data, int length) throws Exception 
    {
        if (connection.isAdminMode())
        {
            String command = new String(data, 0, length);
            command = command.trim();
            PrintStream out = new PrintStream(connection.getOutput());
            if (command.equals("dataMode"))
            {
                connection.setAdminMode(false);
                out.println ("Connection in data mode !!");
            }
            else
            {
                getConsoleManager().processCommand(command, out);
            }
        }
        else
        {
            String command = new String(data, 0, length);
            command = command.trim();
            PrintStream out = new PrintStream(connection.getOutput());
            if (command.equals("adminMode"))
            {
                connection.setAdminMode(true);
                out.println ("Connection in admin mode !!");
            }
        }
    }

    @Override
    public void onConnectionStarted(Connection connection) throws Exception {}

    @Override
    public void onConnectionEnded(Connection connection) throws Exception {}
    
    @Override
    public void onConnectionDataSent(Connection connection, byte[] data, int length) throws Exception {}
}
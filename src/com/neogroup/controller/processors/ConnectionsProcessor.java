
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
        if (connection.isLocal())
        {
            String command = new String(data, 0, length);
            command = command.trim();
            PrintStream out = new PrintStream(connection.getOutput());
            if (command.equals("emulateDevice"))
            {
                connection.setLocal(false);
                out.println ("Device emulation activated !!");
            }
            else
            {
                getConsoleManager().processCommand(command, out);
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
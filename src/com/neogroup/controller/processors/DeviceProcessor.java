
package com.neogroup.controller.processors;

import com.neogroup.controller.Connection;
import com.neogroup.controller.Connection.ConnectionListener;
import com.neogroup.controller.ConsoleManager;
import com.neogroup.utils.StringUtils;
import java.io.PrintStream;
import java.util.List;

public class DeviceProcessor extends Processor implements ConnectionListener, ConsoleManager.ConsoleListener
{ 
    @Override
    public void onStarted() 
    {
        getConnectionManager().addConnectionListener(this);
        getConsoleManager().addConsoleListener(this);
    }

    @Override
    public void onStopped() 
    {
        getConnectionManager().removeConnectionListener(this);
        getConsoleManager().removeConsoleListener(this);
    }
        
    @Override
    public void onConnectionStarted(Connection connection) throws Exception 
    {
    }

    @Override
    public void onConnectionEnded(Connection connection) throws Exception 
    {
    }

    @Override
    public void onConnectionDataReceived(Connection connection, byte[] data, int length) throws Exception 
    {
        if (!connection.isAdminMode())
        {
            String datagram = (connection.getIdentifier() >= 0? StringUtils.padLeft(Integer.toHexString(connection.getIdentifier()), 4, '0') : "0000") + StringUtils.getHexStringFromByteArray(data, length);
            String responseDatagram = getScriptsManager().executeAction("device/" + getApplication().getName() + "/notifyPackage", datagram);
            if (!responseDatagram.isEmpty())
            {
                if (connection.getIdentifier() < 0)
                    connection.setIdentifier(Integer.parseInt(responseDatagram.substring(0, 4), 16));
                String responseData = responseDatagram.substring(4);
                if (!responseData.isEmpty())
                    sendPackage(responseData, connection);
            }
        }
    }

    @Override
    public void onConnectionDataSent(Connection connection, byte[] data, int length) throws Exception 
    {
    }
    
    @Override
    public void onCommandEntered(String command, List<String> commandArguments, PrintStream out) 
    {
        if (command.equals("sendPackage") || command.equals("sp"))
        {
            if (commandArguments.size() < 2)
            {
                out.println ("USAGE: sendPackage [HEXAPACKAGE] [IDENTIFIER]");
            }
            else
            {
                try
                {
                    sendPackage (commandArguments.get(0), Integer.parseInt(commandArguments.get(1)));
                    out.println ("Package sent succesfully !!");
                }
                catch (Exception ex)
                {
                    out.println ("Error sending the package: " + ex.getMessage());
                }
            }
        }
    }
    
    public void sendPackage (String data, int connectionIdentifier) throws Exception
    {
        Connection connection = getConnectionManager().getConnectionByIdentifier(connectionIdentifier);
        if (connection == null)
            throw new Exception ("No connection with identifier \"" + connectionIdentifier + "\"");
        sendPackage (data, connection);
    }
    
    public void sendPackage (String data, Connection connection) throws Exception
    {
        connection.sendData(StringUtils.getByteArrayFromHexString(data));
    }
}
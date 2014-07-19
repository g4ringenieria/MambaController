
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
        if (!connection.isLocal())
        {
            String datagram = StringUtils.getHexStringFromByteArray(data, length);
            String responseDatagram = getCommandManager().executeAction("device/" + getApplication().getName() + "/notifyPackage", datagram);
            try
            {
                sendPackage(responseDatagram, connection);
            }
            catch (Exception exception)
            {
                getLogger().warning("Datagram \"" + responseDatagram + "\" could not be sent !!. " + exception.getMessage());
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
            if (commandArguments.size() == 0)
            {
                out.println ("USAGE: sendPackage [HEXAPACKAGE]");
            }
            else
            {
                try
                {
                    sendPackage (commandArguments.get(0));
                    out.println ("Package sent succesfully !!");
                }
                catch (Exception ex)
                {
                    out.println ("Error sending the package: " + ex.getMessage());
                }
            }
        }
    }
    
    public void sendPackage (String datagram) throws Exception
    {
        sendPackage (datagram, null);
    }
    
    public void sendPackage (String datagram, Connection connection) throws Exception
    {
        if (datagram.isEmpty())
            throw new Exception ("Datagram is empty !!");
                    
        if (connection != null)
        {
            if (connection.getIdentifier() < 0)
            {
                String idField = datagram.substring(0, 4);
                int identifier = Integer.parseInt(idField, 16);
                connection.setIdentifier(identifier);
            }
        }
        else
        {
            String idField = datagram.substring(0, 4);
            int identifier = Integer.parseInt(idField, 16);
            connection = getConnectionManager().getConnectionByIdentifier(identifier);
            if (connection == null)
                throw new Exception ("No connection with identifier \"" + identifier + "\"");
        }

        String datagramPackage = datagram.substring(4);
        if (!datagramPackage.isEmpty())
            connection.sendData(StringUtils.getByteArrayFromHexString(datagramPackage));
    }
}
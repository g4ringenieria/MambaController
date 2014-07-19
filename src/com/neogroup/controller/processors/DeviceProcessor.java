package com.neogroup.controller.processors;

import com.neogroup.controller.Connection;
import com.neogroup.controller.Connection.ConnectionListener;
import com.neogroup.utils.StringUtils;

public class DeviceProcessor extends Processor implements ConnectionListener
{ 
    @Override
    public void onStarted() 
    {
        getConnectionManager().addConnectionListener(this);
    }

    @Override
    public void onStopped() 
    {
        getConnectionManager().removeConnectionListener(this);
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
            String response = getCommandManager().executeAction(getApplication().getActionName(), datagram);
            if (!response.isEmpty())
            {
                if (connection.getIdentifier() < 0)
                {
                    String idField = response.substring(0, 4);
                    int identifier = Integer.parseInt(idField, 16);
                    connection.setIdentifier(identifier);
                }
                
                String responseDatagram = response.substring(4);
                if (!responseDatagram.isEmpty())
                    connection.sendData(StringUtils.getByteArrayFromHexString(responseDatagram));
            }
        }
    }

    @Override
    public void onConnectionDataSent(Connection connection, byte[] data, int length) throws Exception 
    {
    }
}
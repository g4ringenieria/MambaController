package com.neogroup.controller.processors;

import com.neogroup.controller.Connection;
import com.neogroup.controller.Connection.ConnectionListener;

public abstract class DeviceProcessor extends Processor implements ConnectionListener
{
    protected static final int REPORTTYPE_POLL = 1;
    protected static final int REPORTTYPE_TIMEREPORT = 2;
    protected static final int REPORTTYPE_DISTANCEREPORT = 3;
       
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
            datagramReceived(connection, data, length);
        }
    }

    @Override
    public void onConnectionDataSent(Connection connection, byte[] data, int length) throws Exception 
    {
    }
    
    protected abstract void datagramReceived (Connection connection, byte[] data, int length) throws Exception;
}
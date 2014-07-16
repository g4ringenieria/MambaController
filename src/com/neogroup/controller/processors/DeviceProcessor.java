package com.neogroup.controller.processors;

import com.neogroup.controller.Application;
import com.neogroup.controller.Connection;
import com.neogroup.controller.Connection.ConnectionListener;

public abstract class DeviceProcessor extends Processor implements ConnectionListener
{
    protected static final int REPORTTYPE_POLL = 1;
    protected static final int REPORTTYPE_TIMEREPORT = 2;
    protected static final int REPORTTYPE_DISTANCEREPORT = 3;
    
    private String modelName;
    
    public static void setModelType (String modelName) throws Exception
    {
        boolean processorFound = false;
        for (Processor processor : Application.getInstance().getProcessors())
        {
            if (processor instanceof DeviceProcessor)
            {
                DeviceProcessor deviceProcessor = (DeviceProcessor)processor;
                if (deviceProcessor.getModelName().equals(modelName))
                {
                    deviceProcessor.start();
                    processorFound = true;
                }
                else
                {
                    deviceProcessor.stop();
                }
            }
        }
        if (!processorFound)
            throw new Exception ("Device processor \"" + modelName + "\" not found !!");
    }
    
    public DeviceProcessor (String modelName)
    {
        super(false);
        this.modelName = modelName;
    }
    
    @Override
    public void onStarted() 
    {
        Application.getInstance().getConnectionManager().addConnectionListener(this);
    }

    @Override
    public void onStopped() 
    {
        Application.getInstance().getConnectionManager().removeConnectionListener(this);
    }
    
    public String getModelName() 
    {
        return modelName;
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
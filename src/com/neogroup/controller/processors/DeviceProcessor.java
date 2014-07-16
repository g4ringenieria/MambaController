package com.neogroup.controller.processors;

import com.neogroup.controller.Application;
import com.neogroup.controller.Connection;
import com.neogroup.controller.Connection.ConnectionListener;
import com.neogroup.controller.ConsoleManager.ConsoleListener;
import java.io.PrintStream;
import java.util.List;

public abstract class DeviceProcessor extends Processor implements ConsoleListener, ConnectionListener
{
    protected static final int REPORTTYPE_POLL = 1;
    protected static final int REPORTTYPE_TIMEREPORT = 2;
    protected static final int REPORTTYPE_DISTANCEREPORT = 3;
    
    private String modelName;
    
    public DeviceProcessor (String modelName)
    {
        this.modelName = modelName;
    }
    
    @Override
    public void start() 
    {
        Application.getInstance().getConsoleManager().addConsoleListener(this);
    }

    @Override
    public void stop() 
    {
        Application.getInstance().getConsoleManager().removeConsoleListener(this);
        Application.getInstance().getConnectionManager().removeConnectionListener(this);
    }
    
    @Override
    public void onCommandEntered(String command, List<String> commandArguments, PrintStream out)
    {
        if (command.equals("enable"))
        {
            if (commandArguments.size() == 0)
            {
                out.println ("USAGE: enable [MODELNAME]");
            }
            else
            {
                String modelName = commandArguments.get(0);
                if (modelName.equals(this.modelName))
                {
                    Application.getInstance().getConnectionManager().addConnectionListener(this);
                    out.println ("Device processor \"" + modelName + "\" successfully enabled !!");
                }
            }
        }
        else if (command.equals("disable"))
        {
            if (commandArguments.size() == 0)
            {
                out.println ("USAGE: disable [MODELNAME]");
            }
            else
            {
                String modelName = commandArguments.get(0);
                if (modelName.equals(this.modelName))
                {
                    Application.getInstance().getConnectionManager().removeConnectionListener(this);
                    out.println ("Device processor \"" + modelName + "\" successfully disabled !!");
                }
            }
        }
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
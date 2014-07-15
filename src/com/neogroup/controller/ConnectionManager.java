
package com.neogroup.controller;

import com.neogroup.controller.Connection.ConnectionListener;
import com.neogroup.utils.StringUtils;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.EventListenerList;

public class ConnectionManager implements ConnectionListener
{
    protected EventListenerList listeners = new EventListenerList();
    private ServerSocket serverSocket;
    private final List<Connection> connections;
    private boolean active;
    private boolean logging;
    private int port;
    private Thread connectionThread;
    
    public ConnectionManager ()
    {
        active = false;
        logging = false;        
        connections = new ArrayList<Connection>();
    }

    public boolean isLogging ()
    {
        return logging;
    }

    public void setLogging (boolean logging)
    {
        this.logging = logging;
    }
    
    public int getPort() 
    {
        return port;
    }
    
    public void setPort(int port) 
    {
        this.port = port;
    }
    
    public void start ()
    {
        startConnectionThread ();
    }
    
    public void stop ()
    {
        stopConnectionThread ();
    }
    
    private void startConnectionThread ()
    {
        if (connectionThread == null)
        {
            connectionThread = new Thread()
            {
                @Override
                public void run()
                {
                    active = true;
                    Application.getInstance().getLogger().info("ConnectionManager started !!");
                    while (active)
                    {
                        try 
                        {
                            serverSocket = new ServerSocket(port);
                            while (active)
                            {
                                Socket connectionSocket = serverSocket.accept();
                                Connection connection = new Connection(ConnectionManager.this, connectionSocket);
                                connection.addConnectionListener(ConnectionManager.this);
                                connection.start();
                            }
                        } 
                        catch (Exception e) 
                        {
                            if (active)
                                try { Thread.sleep(10000); } catch (Exception ex) {}
                        }
                    }
                    Application.getInstance().getLogger().info("ConnectionManager stopped !!");
                }
            };
            connectionThread.start();
        }
    }
    
    private void stopConnectionThread ()
    {
        active = false;
        if (serverSocket != null)
        {
            try { serverSocket.close(); } catch (Exception ex) {}
            serverSocket = null;
        }
        if (connectionThread != null)
        {
            try { connectionThread.join(); } catch (Exception ex) {}
            connectionThread = null;
        }
    }
    
    public void sendToConnection (int identifier, byte[] data) throws Exception
    {
        Connection connection = getConnectionByIdentifier(identifier);
        if (connection != null)
        {
            sendToConnection(connection, data);
        }
        else
        {
            throw new Exception ("Connection with identifier \"" + identifier + "\" not found !!");
        }
    }
    
    public void sendToConnection (Connection connection, byte[] data) throws Exception
    {
        connection.sendData(data);
    }
    
    public void closeConnection (int identifier) throws Exception
    {
        Connection connection = getConnectionByIdentifier(identifier);
        if (connection != null)
        {
            closeConnection(connection);
        }
        else
        {
            throw new Exception ("Connection with identifier \"" + identifier + "\" not found !!");
        }
    }
    
    public void closeConnection (Connection connection)
    {
        connection.closeConnection();
    }
    
    public List<Connection> getConnections()
    {
        return connections;
    }
    
    public Connection getConnectionByIdentifier (int connectionIdentifier)
    {
        Connection identifierConnection = null;
        synchronized (connections)
        {
            for (Connection connection : connections)
            {
                if (connection.getIdentifier() == connectionIdentifier)
                {
                    identifierConnection = connection;
                    break;
                }
            }
        }
        return identifierConnection;
    }

    public void addConnectionListener(ConnectionListener listener)
    {
        listeners.add(ConnectionListener.class, listener);
    }

    public void removeConnectionListener(ConnectionListener listener)
    {
        listeners.remove(ConnectionListener.class, listener);
    }
    
    @Override
    public void onConnectionStarted (Connection connection)
    {
        synchronized (connections)
        {
            connections.add(connection);
        }
        if (logging)
            Application.getInstance().getLogger().info("Connection [" + connection + "] connected !!");
        for (ConnectionListener listener : listeners.getListeners(ConnectionListener.class))
        {
            try
            {
                listener.onConnectionStarted(connection);
            }
            catch (Exception exception)
            {
                Application.getInstance().getLogger().warning("Error processing connection Started. Ex: " + exception.getMessage());
            }
        }
    }

    @Override
    public void onConnectionEnded (Connection connection)
    {
        synchronized (connections)
        {
            connections.remove(connection);
        }
        if (logging)
            Application.getInstance().getLogger().info("Connection [" + connection + "] disconnected !!");
        for (ConnectionListener listener : listeners.getListeners(ConnectionListener.class))
        {
            try
            {
                listener.onConnectionEnded(connection);
            }
            catch (Exception exception)
            {
                Application.getInstance().getLogger().warning("Error processing connection Ended. Ex: " + exception.getMessage());
            }
        }
    }

    @Override
    public void onConnectionDataReceived (Connection connection, byte[] data, int length)
    {
        if (logging)
            Application.getInstance().getLogger().info("Received from connection [" + connection + "]: " + StringUtils.getHexStringFromByteArray(data, length));
        for (ConnectionListener listener : listeners.getListeners(ConnectionListener.class))
        {
            try
            {
                listener.onConnectionDataReceived(connection, data, length);
            }
            catch (Exception exception)
            {
                Application.getInstance().getLogger().warning("Error processing package: " + StringUtils.getHexStringFromByteArray(data, length) + " Ex: " + exception.getMessage());
            }
        }
    }

    @Override
    public void onConnectionDataSent (Connection connection, byte[] data, int length)
    {
        if (logging)
            Application.getInstance().getLogger().info("Sent to connection [" + connection + "]: " + StringUtils.getHexStringFromByteArray(data, length));
        for (ConnectionListener listener : listeners.getListeners(ConnectionListener.class))
        {
            try
            {
                listener.onConnectionDataSent(connection, data, length);
            }
            catch (Exception exception)
            {
                Application.getInstance().getLogger().warning("Error processing sent package: " + StringUtils.getHexStringFromByteArray(data, length) + " Ex: " + exception.getMessage());
            }
        }        
    }
}
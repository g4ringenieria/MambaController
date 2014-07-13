
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
    private List<Connection> connections;
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
    
    private void stopConnectionThread ()
    {
        active = false;
        if (serverSocket != null)
        {
            try { serverSocket.close(); } catch (Exception ex) {}
            serverSocket = null;
        }
        if (connectionThread != null)
            try { connectionThread.join(); } catch (Exception ex) {}
    }
    
    public void sendToConnection (int identifier, byte[] data)
    {
        Connection connection = getConnectionByIdentifier(identifier);
        if (connection != null)
            sendToConnection(connection, data);
    }
    
    public void sendToConnection (Connection connection, byte[] data)
    {
        if (connections.indexOf(connection) >= 0)
            connection.sendData(data);
    }
    
    public Connection getConnectionByIdentifier (int clientId)
    {
        Connection identifierConnection = null;
        for (Connection connection : connections)
        {
            if (connection.getIdentifier() == clientId)
            {
                identifierConnection = connection;
                break;
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
    public void onConnectionStarted (Connection client)
    {
        connections.add(client);
        for (ConnectionListener listener : listeners.getListeners(ConnectionListener.class))
        {
            try
            {
                listener.onConnectionStarted(client);
            }
            catch (Exception exception)
            {
                Application.getInstance().getLogger().warning("Error processing connection Started. Ex: " + exception.getMessage());
            }
        }
        Application.getInstance().getLogger().info("Client [" + client + "] connected !!");
    }

    @Override
    public void onConnectionEnded (Connection client)
    {
        connections.remove(client);        
        for (ConnectionListener listener : listeners.getListeners(ConnectionListener.class))
        {
            try
            {
                listener.onConnectionEnded(client);
            }
            catch (Exception exception)
            {
                Application.getInstance().getLogger().warning("Error processing connection Ended. Ex: " + exception.getMessage());
            }
        }
        Application.getInstance().getLogger().info("Client [" + client + "] disconnected !!");
    }

    @Override
    public void onConnectionDataReceived (Connection client, byte[] data, int length)
    {
        Application.getInstance().getLogger().info("Received from client [" + client + "]: " + StringUtils.getHexStringFromByteArray(data, length));
        for (ConnectionListener listener : listeners.getListeners(ConnectionListener.class))
        {
            try
            {
                listener.onConnectionDataReceived(client, data, length);
            }
            catch (Exception exception)
            {
                Application.getInstance().getLogger().warning("Error processing package: " + StringUtils.getHexStringFromByteArray(data, length) + " Ex: " + exception.getMessage());
            }
        }
    }

    @Override
    public void onConnectionDataSent (Connection client, byte[] data, int length)
    {
        for (ConnectionListener listener : listeners.getListeners(ConnectionListener.class))
        {
            try
            {
                listener.onConnectionDataSent(client, data, length);
            }
            catch (Exception exception)
            {
                Application.getInstance().getLogger().warning("Error processing sent package: " + StringUtils.getHexStringFromByteArray(data, length) + " Ex: " + exception.getMessage());
            }
        }
        Application.getInstance().getLogger().info("Sent to client [" + client + "]: " + StringUtils.getHexStringFromByteArray(data, length));
    }
}
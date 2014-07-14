
package com.neogroup.controller;

import com.neogroup.utils.StringUtils;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.EventListener;
import javax.swing.event.EventListenerList;

public class Connection extends Thread
{
    protected EventListenerList listeners = new EventListenerList();
    private int identifier;
    private String socketAddress;
    private Socket socket;
    private ConnectionManager connectionManager;
    private DataOutputStream output;
    private DataInputStream input;

    public Connection (ConnectionManager connectionManager, Socket socket)
    {
        this.identifier = -1;
        this.socket = socket;
        this.socketAddress = socket.getInetAddress().getHostAddress();
        this.connectionManager = connectionManager;
    }
    
    @Override
    public void destroy ()
    {
        closeConnection();
        while (listeners.getListenerCount() > 0)
            removeConnectionListener(listeners.getListeners(ConnectionListener.class)[0]);
        listeners = null;
    }
    
    @Override
    public void run ()
    {
        boolean handlerOpen = false;
        try
        {   
            output = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());
            handlerOpen = true;
            fireConnectionStarted();
        }
        catch (Exception ex1)
        {
            Application.getInstance().getLogger().warning("Error starting client. Ex: " + ex1.getMessage());
        }
        
        if (handlerOpen)
        {
            try
            {
                byte[] bytes = new byte[500];
                int bytesRead = 0;
                while ((bytesRead=input.read(bytes)) > 0)
                {
                    fireConnectionDataReceived(bytes, bytesRead);
                }
            }
            catch (Exception ex)
            {
            }
            finally
            {
                closeConnection();
                fireConnectionEnded();
            }
        }
        
        destroy ();
    }
    
    public synchronized void closeConnection ()
    {
        if (output != null)
        {
            try { output.close(); } catch (Exception ex) {}
            output = null;
        }
        if (input != null)
        {
            try { input.close(); } catch (Exception ex) {}
            input = null;
        }
        if (socket != null)
        {
            try { socket.close(); } catch (Exception ex) {}
            socket = null;
        }
    }
    
    public void sendData (byte[] dataToSend)
    {
        sendData(dataToSend, dataToSend.length);
    }
    
    public void sendData (byte[] dataToSend, int length)
    {
        if (output != null)
        {
            try
            {
                output.write(dataToSend);
                fireConnectionDataSent(dataToSend, length);
            }
            catch (Exception exception)
            {
                Application.getInstance().getLogger().warning("Error sending package: " + StringUtils.getHexStringFromByteArray(dataToSend, length));
                closeConnection();
            }
        }
    }

    public int getIdentifier ()
    {
        return identifier;
    }

    public void setIdentifier (int identifier)
    {
        this.identifier = identifier;
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }
    
    public void addConnectionListener(ConnectionListener listener)
    {
        listeners.add(ConnectionListener.class, listener);
    }

    public void removeConnectionListener(ConnectionListener listener)
    {
        listeners.remove(ConnectionListener.class, listener);
    }

    private void fireConnectionStarted ()
    {
        for (ConnectionListener listener : listeners.getListeners(ConnectionListener.class))
        {
            try
            {
                listener.onConnectionStarted(this);
            }
            catch (Exception exception)
            {
                Application.getInstance().getLogger().warning("Error processing connection Start. Ex: " + exception.getMessage());
            }
        }
    }

    private void fireConnectionEnded ()
    {
        for (ConnectionListener listener : listeners.getListeners(ConnectionListener.class))
        {
            try
            {
                listener.onConnectionEnded(this);
            }
            catch (Exception exception)
            {
                Application.getInstance().getLogger().warning("Error processing connection End. Ex: " + exception.getMessage());
            }
        }
    }

    private void fireConnectionDataReceived (byte[] data, int length)
    {
        for (ConnectionListener listener : listeners.getListeners(ConnectionListener.class))
        {
            try
            {
                listener.onConnectionDataReceived(this, data, length);
            }
            catch (Exception exception)
            {
                Application.getInstance().getLogger().warning("Error processing package: " + StringUtils.getHexStringFromByteArray(data, length) + " Ex: " + exception.getMessage());
            }
        }
    }
    
    private void fireConnectionDataSent (byte[] data, int length)
    {
        for (ConnectionListener listener : listeners.getListeners(ConnectionListener.class))
        {
            try
            {
                listener.onConnectionDataSent(this, data, length);
            }
            catch (Exception exception)
            {
                Application.getInstance().getLogger().warning("Error processing sent package: " + StringUtils.getHexStringFromByteArray(data, length) + " Ex: " + exception.getMessage());
            }
        }
    }

    @Override
    public String toString ()
    {
        String connectionId = "";
        connectionId += (this.identifier > 0)? this.identifier : "?";
        connectionId += "@";
        connectionId += this.socketAddress;
        return connectionId;
    }

    public interface ConnectionListener extends EventListener
    {
        public void onConnectionStarted (Connection connection) throws Exception;
        public void onConnectionEnded (Connection connection) throws Exception;
        public void onConnectionDataReceived (Connection connection, byte[] data, int length) throws Exception;
        public void onConnectionDataSent (Connection connection, byte[] data, int length) throws Exception;
    }
}
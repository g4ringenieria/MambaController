
package com.neogroup.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ScriptsManager 
{
    public static final int MODE_LOCAL = 0;
    public static final int MODE_REMOTE = 0;
    
    private int mode;
    private String host;
    private int port;
    private String localScriptsDir;
    private String localScriptsFilename;
    
    public ScriptsManager ()
    {
        mode = MODE_LOCAL;
        host = "localhost";
        port = 80;
        localScriptsDir = "./";
        localScriptsFilename = "executeAction.php";
    }

    public String getHost() 
    {
        return host;
    }

    public void setHost(String host) 
    {
        this.host = host;
    }

    public int getPort() 
    {
        return port;
    }

    public void setPort(int port) 
    {
        this.port = port;
    }

    public int getMode() 
    {
        return mode;
    }

    public void setMode(int mode) 
    {
        this.mode = mode;
    }
    
    public String getLocalScriptsDir() 
    {
        return localScriptsDir;
    }

    public void setLocalScriptsDir(String baseScriptsDir) 
    {
        this.localScriptsDir = baseScriptsDir;
    }

    public String getLocalScriptsFilename() 
    {
        return localScriptsFilename;
    }

    public void setLocalScriptsFilename(String localScriptsFilename)
    {
        this.localScriptsFilename = localScriptsFilename;
    }

    public String executeAction (String action, String... arguments) throws Exception
    {    
        StringBuilder response = new StringBuilder();
        if (mode == MODE_LOCAL)
        {
            String[] tokens = new String[arguments.length + 3];
            tokens[0] = "php";
            tokens[1] = localScriptsDir + localScriptsFilename;
            tokens[2] = action;
            int index = 3;
            for (String argument : arguments)
                tokens[index++] = argument;
            Process process = Runtime.getRuntime().exec(tokens);
            process.waitFor();
            String responseLine;
            BufferedReader inputReader = null;
            try
            {
                inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((responseLine = inputReader.readLine()) != null)
                    response.append(responseLine);
            }
            catch (Exception ex) {}
            try { inputReader.close(); } catch (Exception ex) {}
        }
        return response.toString();
    }
}
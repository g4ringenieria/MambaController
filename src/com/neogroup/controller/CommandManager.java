
package com.neogroup.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CommandManager 
{
    private String localScriptsDir;
    private String scriptName;
    
    public CommandManager ()
    {
        localScriptsDir = "../../NeoGroup/";
        scriptName = "executeAction.php";
    }

    public String getLocalScriptsDir() 
    {
        return localScriptsDir;
    }

    public void setLocalScriptsDir(String baseScriptsDir) 
    {
        this.localScriptsDir = baseScriptsDir;
    }

    public String getScriptName() 
    {
        return scriptName;
    }

    public void setScriptName(String scriptName) 
    {
        this.scriptName = scriptName;
    }

    public String executeAction (String action, String... arguments) throws Exception
    {    
        String[] tokens = new String[arguments.length + 3];
        tokens[0] = "php";
        tokens[1] = localScriptsDir + scriptName;
        tokens[2] = action;
        int index = 3;
        for (String argument : arguments)
            tokens[index++] = argument;
        Process process = Runtime.getRuntime().exec(tokens);
        process.waitFor();
        
        String responseLine;
        StringBuilder response = new StringBuilder();
        BufferedReader inputReader = null;
        try
        {
            inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((responseLine = inputReader.readLine()) != null)
                response.append(responseLine);
        }
        catch (Exception ex) {}
        try { inputReader.close(); } catch (Exception ex) {}
        return response.toString();
    }
}
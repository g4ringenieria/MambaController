
package com.neogroup.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CommandManager 
{
    private String localScriptsDir;
    private String scriptName;
    private boolean debugMode;
    
    public CommandManager ()
    {
        localScriptsDir = "../../NeoGroup/";
        scriptName = "command.php";
        debugMode = false;
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

    public boolean isDebugMode() 
    {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) 
    {
        this.debugMode = debugMode;
    }
    
    public void executeAction (String action, String... arguments) throws Exception
    {    
        String[] tokens = new String[arguments.length + 3];
        tokens[0] = "php";
        tokens[1] = localScriptsDir + scriptName;
        tokens[2] = action;
        int index = 3;
        for (String argument : arguments)
            tokens[index++] = argument;
        
        if (debugMode)
        {
            StringBuilder debugString = new StringBuilder();
            debugString.append("Executing command: \"");
            boolean first = true;
            for (String token : tokens)
            {
                if (!first)
                    debugString.append (' ');
                debugString.append (token);
                first = false;
            }
            debugString.append("\" response: \"");
            
            Process process = Runtime.getRuntime().exec(tokens);
            process.waitFor();
            String line;
            
            BufferedReader errorReader = null;
            try
            {
                errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                while((line = errorReader.readLine()) != null)
                    debugString.append(line);
            }
            catch (Exception ex)
            {
                try { errorReader.close(); } catch (Exception ex1) {}
                errorReader = null;
            }
            
            BufferedReader inputReader = null;
            try
            {
                inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while((line=inputReader.readLine()) != null)
                    debugString.append(line);
            }
            catch (Exception ex)
            {
                try { inputReader.close(); } catch (Exception ex2) {}
                inputReader = null;
            }
            
            debugString.append("\"");
            Application.getInstance().getLogger().info(debugString.toString());
        }
        else
        {
            Runtime.getRuntime().exec(tokens);
        }
    }
}
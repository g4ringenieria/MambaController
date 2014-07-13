
package com.neogroup.utils;

public abstract class StringUtils 
{
    public static String getHexString (byte[] bytes, int length)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++)
            sb.append(String.format("%02x", bytes[i]));
        return sb.toString();
    }
}

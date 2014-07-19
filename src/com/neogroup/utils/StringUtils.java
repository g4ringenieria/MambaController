
package com.neogroup.utils;

public abstract class StringUtils 
{
    public static String getHexStringFromByteArray (byte[] bytes)
    {
        return getHexStringFromByteArray(bytes, bytes.length);
    }
    
    public static String getHexStringFromByteArray (byte[] bytes, int length)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++)
            sb.append(String.format("%02x", bytes[i]));
        return sb.toString();
    }
    
    public static byte[] getByteArrayFromHexString (String hexString) 
    {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) 
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i+1), 16));
        return data;
    }
    
    public static String padLeft (String str, int count, char fill)
    {
        return String.format("%" + count + "s", str).replace(' ', fill);
    }
    
    public static String padRight (String str, int count, char fill)
    {
        return String.format("%-" + count + "s", str).replace(' ', fill);
    }
}


package com.neogroup.controller.processors;

import com.neogroup.controller.Connection;
import java.util.Calendar;
import java.util.TimeZone;

public class TT8750DeviceProcessor extends DeviceProcessor
{
    private static final int DATAGRAMTYPE_DEFAULT = 8;
    private static final int DATAGRAMTYPE_SERIALPORT = 2;
    
    @Override
    public void datagramReceived(Connection connection, byte[] data, int length) throws Exception
    {
        int datagramType = ord(data[4]);
       
        switch (datagramType)
        {
            case DATAGRAMTYPE_DEFAULT:
                if (ord(data[5]) == 0)
                {
                    StringBuilder idField = new StringBuilder();
                    idField.append((char)data[27]);
                    idField.append((char)data[28]);
                    idField.append((char)data[29]);
                    idField.append((char)data[30]);
                    idField.append((char)data[31]);
                    int deviceId = Integer.parseInt(idField.toString());
                    connection.setIdentifier(deviceId);
                }
                else
                {
                    StringBuilder idField = new StringBuilder();
                    idField.append((char)data[18]);
                    idField.append((char)data[19]);
                    idField.append((char)data[20]);
                    idField.append((char)data[21]);
                    idField.append((char)data[22]);
                    int deviceId = Integer.parseInt(idField.toString());
                    int eventId = ord(data[14]) + (ord(data[13]) << 8);
                    int reportTypeId = getReportTypeByEvent(eventId);
                    int ios = ord(data[24]) + (ord(data[23]) << 8);
                    int validity = ord(data[25]);
                    double latitude = getCoordinate(data[26], data[27], data[28], data[29]);
                    double longitude = getCoordinate(data[30], data[31], data[32], data[33]);
                    double speed = ((ord(data[35]) + (ord(data[34]) << 8)) / 10) * 1.8;
                    int course = ((ord(data[37]) + (ord(data[36]) << 8)) / 10);
                    int altitude = (ord(data[40]) + (ord(data[39]) << 8) + (ord(data[38]) << 16)) / 10;
                    int odometer = (ord(data[44]) + (ord(data[43]) << 8) + (ord(data[42]) << 16) + (ord(data[41]) << 24)) / 10;
                    Calendar date = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    date.set(ord(data[45])+2000, ord(data[46]) - 1, ord(data[47]), ord(data[48]), ord(data[49]), ord(data[50]));
                    long dateTime = date.getTimeInMillis() / 1000;
                            
                    StringBuilder reportJson = new StringBuilder();
                    reportJson.append('{');
                    reportJson.append("\"reportClassTypeId\": ").append(1);
                    reportJson.append(",\"reportTypeId\": ").append(reportTypeId);
                    reportJson.append(",\"deviceId\": ").append(deviceId);
                    reportJson.append(",\"validity\": ").append(validity);
                    reportJson.append(",\"latitude\": ").append(latitude);
                    reportJson.append(",\"longitude\": ").append(longitude);
                    reportJson.append(",\"speed\": ").append(speed);
                    reportJson.append(",\"course\": ").append(course);
                    reportJson.append(",\"altitude\": ").append(altitude);
                    reportJson.append(",\"odometer\": ").append(odometer);
                    reportJson.append(",\"date\": ").append(dateTime);
                    reportJson.append('}');
                    getCommandManager().executeAction("reports/createResource", reportJson.toString());
                }
                break;
            case DATAGRAMTYPE_SERIALPORT:
//                $datagram = substr($datagram, 7);
//		  $datagram = "0035000a08100418e0be4f" . $datagram;
//                $datagram = hex2bin($datagram);
//                $deviceId = intval((substr($datagram, 15, 8)));
//                $eventId = ord($datagram{14}) + (ord($datagram{13}) << 8);
//                $this->getLogger()->debug("Serial port package => equipo: " . $deviceId . "; evento: " . $eventId);
                break;
        }
    }

    private int ord (byte b)
    {
        return (int)b & 0xFF;
    }
    
    private float getCoordinate (byte b1, byte b2, byte b3, byte b4)
    {
        boolean isNegative = false;
        int a = ord(b1);
        int b = ord(b2);
        int c = ord(b3);
        int d = ord(b4);
        if (a > 128)
        {
            isNegative = true;
            a = 255 - a;
            b = 255 - b;
            c = 255 - c;
            d = 255 - d;
        }
        String decimalCoordinate = String.valueOf(d + (c << 8) + (b << 16) + (a << 24));
        if (decimalCoordinate.length() < 8)
        {
            int paddingCount = 8 - decimalCoordinate.length();
            for (int i = 0; i < paddingCount; i++)
                decimalCoordinate = "0" + decimalCoordinate;
        }
        float degrees = Float.parseFloat(decimalCoordinate.substring(0, decimalCoordinate.length() - 6));
        float minutes = Float.parseFloat(decimalCoordinate.substring(decimalCoordinate.length() - 6)) / 10000;
        float coordinateDegrees = degrees + (minutes/60);
        return (isNegative)? -coordinateDegrees : coordinateDegrees;
    }
    
    private int getReportTypeByEvent (int eventId)
    {
        int reportType = 0;
        switch (eventId)
        {
            case 21: reportType = REPORTTYPE_TIMEREPORT; break;
        }
        return reportType;
    }
}
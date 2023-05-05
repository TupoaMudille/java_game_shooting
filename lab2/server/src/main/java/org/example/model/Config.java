package org.example.model;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class Config
{
    public final int SIZEX = 600;
    public final int SIZEY = 600;
    public final int PORT = 3451;
    public final InetAddress ip;
    public Config()
    {
        try
        {
            ip = InetAddress.getLocalHost();
        }
        catch (UnknownHostException e)
        {
            throw new RuntimeException(e);
        }
    }

}

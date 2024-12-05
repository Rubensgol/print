package test;

import java.io.IOException;

import geraConfig.CriaConfig;

public class geraConfigExe 
{
    public static void  main (String[] args)
    {
        CriaConfig gConfig = new CriaConfig();
        try
        {
            gConfig.geraConfig();
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }    
}

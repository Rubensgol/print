package test;

import java.io.IOException;

import controler.business.atualizar.update4j.CriaConfig;

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

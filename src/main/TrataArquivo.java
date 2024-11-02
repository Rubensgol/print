package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import controler.ITrataArquivo;
import util.Util;

public class TrataArquivo implements ITrataArquivo
{
    public void salvaTxt(List<Integer> nfLidas)
    {
        try
        {
            FileWriter myWriter = new FileWriter(Util.getDataFormatadaSemBarra() + ".txt");

            for (Integer integer : nfLidas)
                myWriter.write(integer.toString());

            myWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public List<Integer> carregaArquivo()
    {
        List<Integer> nfs = new ArrayList<>();

        String fileName = Util.getDataFormatadaSemBarra();
        File myObj = new File(fileName + ".txt");

        if (myObj.exists())
        {
            try 
            {
                Scanner myReader = new Scanner(myObj);
                String data;
    
                while (myReader.hasNextLine())
                {
                    data = myReader.nextLine();
                    nfs.add(Integer.parseInt(data));
                }
    
                myReader.close();
            }
            catch (FileNotFoundException e) 
            {
                e.printStackTrace();
            }
        }
        else
        {
            try 
            {
                myObj.createNewFile();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }

        return nfs;
    }
}

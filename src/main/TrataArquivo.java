package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import controler.ITrataArquivo;
import util.Util;

public class TrataArquivo implements ITrataArquivo
{
    private static final Logger logger = Logger.getLogger(TrataArquivo.class.getName());

    public void salvaTxt(List<Integer> nfLidas)
    {
        try
        {
            logger.info("abrindo arquivo com as nfs lidas parada salvar");
            FileWriter myWriter = new FileWriter(Util.getDataFormatadaSemBarra() + ".txt");

            logger.info("Salvando arquivo com as nfs lidas");
            for (Integer integer : nfLidas)
            {
                logger.info("Salvando a nf: " + integer);
                myWriter.write(integer.toString());
                myWriter.write("\n");
            }

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

        logger.info("Carregando arquivo das nfs");
        String fileName = Util.getDataFormatadaSemBarra();
        File myObj = new File(fileName + ".txt");

        logger.info("Carregado arquivo: " + fileName + ".txt");

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
                    logger.info("Carregado nf: " + data);
                }
    
                myReader.close();
            }
            catch (FileNotFoundException e) 
            {
                logger.severe(e.getMessage());
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

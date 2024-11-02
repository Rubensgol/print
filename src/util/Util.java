package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util 
{
    public static String converteJsonEmString(BufferedReader buffereReader) throws IOException 
    {
        String resposta, jsonEmString = "";

        while ((resposta = buffereReader.readLine()) != null) 
            jsonEmString += resposta;

        return jsonEmString;
    }

    public static String getDataFormatada()
    {
        Date data = new Date();

    	SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");

        return dtf.format(data);
    }

    public static String getDataFormatadaSemBarra()
    {
        Date data = new Date();

    	SimpleDateFormat dtf = new SimpleDateFormat("dd-MM-yyyy");

        return dtf.format(data);
    }
}

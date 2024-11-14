package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    public static String getDataFormatadaMesAnterior()
    {
        Date data = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(data);
        c.add(Calendar.MONTH, -1);

        SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");

        Date dataAnterior = c.getTime();

        return dtf.format(dataAnterior);
    }

    public static boolean comaparaData(String primeiraData)
    {
        if (primeiraData == null)
            return false;

        SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");

        Date data = new Date();
        Date date2 = null;

        try 
        {
            date2 = dtf.parse(primeiraData);
            data = dtf.parse(dtf.format(data));
        } 
        catch (ParseException e) 
        {
            return false;
        }

        int i = data.compareTo(date2);

        if (i == 0)
            return true;
    
        return false;
    }


    public static String getDataFormatadaSemBarra()
    {
        Date data = new Date();

    	SimpleDateFormat dtf = new SimpleDateFormat("dd-MM-yyyy");

        return dtf.format(data);
    }
}

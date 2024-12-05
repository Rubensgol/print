package test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.update4j.Configuration;

import controler.interfaces.ITrataArquivo;
import controler.business.TrataArquivo;
import view.Tela;

public class Program 
{    
    public static void  main (String[] args)
    {
        List<Integer> nfsLidas = new ArrayList<>();
        ITrataArquivo arqv = new TrataArquivo();

        nfsLidas = arqv.carregaArquivo();

		URL configUrl;
        Configuration config = null;
        Reader in;

        try 
        {
            configUrl = new URI("https://raw.githubusercontent.com/rubensgolSecret/print/refs/heads/main/config.xml").toURL();
            in = new InputStreamReader(configUrl.openStream(), StandardCharsets.UTF_8);
            config = Configuration.read(in);
        }
        catch (IOException | URISyntaxException e) 
        {
            System.err.println("Could not load remote config, falling back to local.");

            try 
            {
                in = Files.newBufferedReader(Paths.get("config.xml"));
                config = Configuration.read(in);
            }
            catch (IOException e1) 
            {
                e1.printStackTrace();
            }
        }

    	new Tela(nfsLidas, config);
    }
}
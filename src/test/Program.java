package test;

import java.util.ArrayList;
import java.util.List;

import controler.ITrataArquivo;
import main.TrataArquivo;
import view.Tela;

public class Program 
{    
    public static void  main (String[] args)
    {
        List<Integer> nfsLidas = new ArrayList<>();
        ITrataArquivo arqv = new TrataArquivo();

        nfsLidas = arqv.carregaArquivo();

    	new Tela(nfsLidas);
    }
}
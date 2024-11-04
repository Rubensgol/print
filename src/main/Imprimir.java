package main;

import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import controler.IImprimir;

public class Imprimir implements IImprimir
{
	private static final Logger logger = Logger.getLogger(Imprimir.class.getName());

	public void imprimir(String path) 
	{
        try
        {
        	File pdf = null; 
        	URL url = new URL(path);

        	String tDir = System.getProperty("java.io.tmpdir");

        	String nameFile = tDir + "tmp" + ".pdf";

        	pdf = new File(path, nameFile);
        	pdf.deleteOnExit();
        	FileUtils.copyURLToFile(url, pdf);

			logger.info("imprimindo arquivo: " + path);

        	PDDocument document = Loader.loadPDF(pdf);
      		PrintJob.printFile(document);
        }
        catch (IOException | PrinterException e) 
        {
			logger.severe(e.getMessage());
            e.printStackTrace();
        }
	}
}

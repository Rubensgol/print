package controler.business.imprimir;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import controler.business.TrataArquivo;
import controler.interfaces.IImprimir;

public class ImprimirDesktop implements IImprimir
{
	private static final Logger logger = Logger.getLogger(ImprimirDesktop.class.getName());
	private String printerName;

	public void setPrinterName(String printerName) 
	{
		this.printerName = printerName;
	}

	public void imprimir(String path) 
	{
		try
		{
			File docFile = null;
			PDDocument document = null;
			URI uri = new URI(path);
			URL url = uri.toURL();

			if (path.endsWith(".zip"))
				docFile = TrataArquivo.extraiArquivo(uri);
			else
			{
				String nameFile = FileUtils.getTempDirectoryPath() + "tmp" + ".pdf";

				docFile = new File(nameFile);
				docFile.deleteOnExit();
				FileUtils.copyURLToFile(url, docFile);
			}
			
			logger.info("imprimindo arquivo: " + path);

			document = Loader.loadPDF(docFile);
			PrintJob.printFile(document, printerName);
		}
		catch (Exception e) 
        {
			logger.severe(e.getMessage());
            e.printStackTrace();
        }
	}
}

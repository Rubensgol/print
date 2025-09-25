package controler.business.imprimir;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

public class PrintJob 
{
	public static void printFile(PDDocument document) throws PrinterException 
	{
		printFile(document, null);
	}

	// New method accepting a printer name. If printerName is null or empty, default printer is used.
	public static void printFile(PDDocument document, String printerName) throws PrinterException 
	{
		PrinterJob job = PrinterJob.getPrinterJob();

		job.setPageable(new PDFPageable(document));

		if (printerName != null && !printerName.isEmpty()) 
		{
			PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);

			for (PrintService s : services)
			{
				if (s.getName().equals(printerName)) 
				{
					try 
					{
						job.setPrintService(s);
					} 
					catch (PrinterException e) 
					{
						// If setting the requested print service fails, fall back to default
						System.err.println("Falha ao selecionar impressora '" + printerName + "': " + e.getMessage());
					}

					break;
				}
			}
		}

		job.print();
	}
}

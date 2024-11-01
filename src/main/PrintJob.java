package main;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

public class PrintJob
{
	public static void printFile (PDDocument document) throws PrinterException
	{
		PrinterJob job = PrinterJob.getPrinterJob();

        job.setPageable(new PDFPageable(document));
        job.print();
	}
}

package at.ac.meduniwien.mias.adltoschematron.helpers;

import static at.ac.meduniwien.mias.adltoschematron.helpers.IConstants.*;
import static at.ac.meduniwien.mias.adltoschematron.helpers.Utils.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

import lombok.extern.log4j.Log4j;

@Log4j
public final class Writer {

	private static java.io.Writer bw;
	private static boolean writeToFile;
	private static String outputFile;

	/**
	 * Write the XML header.
	 */
	public static void writeHeader() {

		w("<?xml version=" + OUT_QUOTE + "1.0" + OUT_QUOTE + " encoding=" + OUT_QUOTE + "UTF-8" + OUT_QUOTE + "?>");
		w("<schema xmlns=\"http://purl.oclc.org/dsdl/schematron\" queryBinding=\"xslt2\" xmlns:" + NS_PREFIX + "=" + oquote(NAMESPACE) + ">");
		w(IWriter.INDENT1 + "<ns prefix=" + oquote(NS_PREFIX) + " uri=" + oquote(NAMESPACE) + " />");
		w(IWriter.INDENT1 + "<ns prefix=\"fn\" uri=\"http://www.w3.org/2005/xpath-functions\" />");
		w(IWriter.INDENT1 + "<ns prefix=\"xs\" uri=\"http://www.w3.org/2001/XMLSchema\" />");
		w("");

	}

	/**
	 * Write the XML footer.
	 */
	public static void writeFooter() {
		w("</schema>");
	}

	/**
	 * delete previous generated schematron-file.
	 */
	public static void resetOutput() {
		if (writeToFile) {
			File f = new File(outputFile);

			try {

				if (f.delete()) {

					log.info("Deleted: " + f.getName());

				}

			} catch (SecurityException e) {

				log.error("Could not delete file: " + f.getName());

			}
		}
	}

	public static void start(final boolean writeToFile, final String outputFile) {
		Writer.writeToFile = writeToFile;
		Writer.outputFile = outputFile;
		try {
			if (writeToFile) {
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));
			} else {
				bw = new StringWriter();
			}

		} catch (IOException e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}

	public static String getString() {
		if (!writeToFile) {
			return bw.toString();
		}
		return null;
	}

	public static void stop() {
		if (bw != null) {
			try {
				bw.close();
			} catch (IOException e) {
				log.error(e.getLocalizedMessage(), e);
			}
		}
	}

	/**
	 * writes line to schematron-file.
	 * 
	 * @param string
	 */
	public static void w(final String string) {

		try {

			bw.append(string + IWriter.NEWLINE);

		} catch (IOException e) {

			log.error(e.getLocalizedMessage(), e);

		}

	}

}

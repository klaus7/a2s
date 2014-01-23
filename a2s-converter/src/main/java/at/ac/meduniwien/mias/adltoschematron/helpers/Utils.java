package at.ac.meduniwien.mias.adltoschematron.helpers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import lombok.extern.log4j.Log4j;

import org.apache.commons.lang.StringUtils;

import at.ac.meduniwien.mias.adltoschematron.AdlToSchematronConverter;

@Log4j
public final class Utils implements IConstants {

	public static Properties languageProperties;

	public static final String SVRL_REPORT_XSL = "iso_svrl_for_xslt2.xsl";

	private static final String HTML_REPORT_XSL = "svrl_to_html.xsl";

	public static final String WHITESPACE = " ";

	public static final String POINT = ".";

	/**
	 * Replaces all regular expresssions from the replacement table.
	 * 
	 * @param input input string
	 * @param replacementTable first entry is the regular expression, second entry is the replacement
	 * @return the replaced string
	 */
	public static String replaceAll(final String input, final String... replacementTable) {
		String s = input;
		for (int i = 0; i < replacementTable.length; i += 2) {
			String regex = replacementTable[i];
			String replacement = replacementTable[i + 1];
			s = s.replaceAll(regex, replacement);
		}
		return s;
	}

	public static String returnFirstNotEmpty(final String... items) {
		for (String item : items) {
			if (!StringUtils.isEmpty(item)) {
				return item;
			}
		}
		return null;
	}

	/**
	 * @param key word key
	 * @param expr field of Strings to substitute $i
	 * @return sentence with substituted $1, $2, ... with field from expr
	 */
	public static String lang(final String key, final String... expr) {
		String s = languageProperties.getProperty(key, "");
		for (int i = 0; i < expr.length; i++) {
			s = s.replace("$" + (i + 1), expr[i]);
		}
		return s;
	}

	/**
	 * @param key word key
	 * @return word with key
	 */
	public static String lang(final String key) {
		return languageProperties.getProperty(key, "");
	}

	/**
	 * @param f property file to load
	 * @return loaded property file
	 * @throws IOException property file not found
	 */
	public static Properties loadProperties(final File f) throws IOException {

		Properties properties = new Properties();
		properties.load(new FileInputStream(f));

		return properties;
	}

	/**
	 * 
	 * @param xmlFile
	 * @return assertion errors
	 */
	public static int validateXmlWithSchematronFile(final String xmlFile) {

		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		String schematronFile = AdlToSchematronConverter.outputFile;

		// generate schematron xsl
		String tmpxsl = xsl(schematronFile, Utils.class.getClassLoader().getResourceAsStream(SVRL_REPORT_XSL), null);

		// validate xml input file
		xsl(xmlFile, new ByteArrayInputStream(tmpxsl.getBytes()), AdlToSchematronConverter.reportOutputFile);

		// create html report
		xsl(AdlToSchematronConverter.reportOutputFile, Utils.class.getClassLoader().getResourceAsStream(HTML_REPORT_XSL),
				AdlToSchematronConverter.reportOutputFile + ".html");

		return Reporter.printReportToConsole();

	}

	/**
	 * @param xmlFile xml input file
	 * @param xslFile xsl transformation file
	 * @param outFile output file
	 * @return output iif outFile == null, otherwise null
	 */
	private static String xsl(final String xmlFile, final String xslFile, final String outFile) {
		return xsl(xmlFile, new StreamSource(new File(xslFile)), outFile);
	}

	private static String xsl(final String xmlFile, final InputStream xslStream, final String outFile) {
		return xsl(xmlFile, new StreamSource(xslStream), outFile);
	}

	private static String xsl(final String xmlFile, final Source xslSource, final String outFile) {
		try {

			TransformerFactory tFactory = TransformerFactory.newInstance();

			// 2. Use the TransformerFactory to process the stylesheet Source and
			//		    generate a Transformer.
			Transformer transformer = tFactory.newTransformer(xslSource);

			// 3. Use the Transformer to transform an XML Source and send the
			//		    output to a Result object.

			if (outFile != null) {

				FileOutputStream fs = new FileOutputStream(outFile);
				transformer.transform(new StreamSource(new File(xmlFile)), new StreamResult(fs));
				fs.flush();
				fs.close();

				return null;

			} else {

				StringWriter sw = new StringWriter();
				StreamResult sr = new StreamResult(sw);
				transformer.transform(new StreamSource(new File(xmlFile)), sr);

				return sw.getBuffer().toString();

			}

		} catch (Exception e) {

			log.error(e.getLocalizedMessage(), e);

		}
		return null;
	}

	/**
	 * Handle special rule exceptions to some attributes.
	 * 
	 * @param a
	 * @return
	 */
	@CdaSpecific
	public static String attributeWrapper(final String a) {
		String s = a;
		if (a.equals("iD")) {
			s = "ID";
		}
		return s;
	}

	/**
	 * @param input
	 * @return removes @ character and replaces it with the namespace prefix
	 */
	public static String removeAttributeChar(final String input) {

		return input.replaceAll("@", Utils.NS_PREFIX_);

	}

	/**
	 * Put return string into quotes.
	 * 
	 * @param object
	 * @return
	 */
	public static String quote(final Object object) {
		return IN_QUOTE + object + IN_QUOTE;
	}

	/**
	 * Put return string into quotes.
	 * 
	 * @param object
	 * @return
	 */
	public static String oquote(final Object object) {
		return OUT_QUOTE + object + OUT_QUOTE;
	}

	public static int getInstanceIdentifierSpecificity(final String instanceIdentifier) {
		return instanceIdentifier.split("\\.").length;
	}

}

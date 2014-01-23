package at.ac.meduniwien.mias.adltoschematron;

import org.apache.commons.cli.ParseException;

/**
 * Helper class to start schematron converter.
 * @author Klaus Pfeiffer
 */
public final class Start {
	
	/**
	 * default constructor.
	 */
	private Start() {
	}

	/**
	 * @param args program arguments
	 * @throws ParseException 
	 */
	public static void main(final String[] args) throws ParseException {
		String adlFile = "adl/test/adl-test-entry.basic_types.test.adl";
		String xmlFile = "xml/test/test-entry-basic-success.xml";

		if (args != null && args.length > 1) {
			adlFile = args[0];
			xmlFile = args[1];
		}

		AdlToSchematronConverter.run(adlFile, xmlFile);
	}

}

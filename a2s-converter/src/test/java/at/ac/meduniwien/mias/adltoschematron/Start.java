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
		String adlFile = "adl/elga/HL7-CDA-ClinicalDocument.Ent_Aerztl_Full_Sup_v_1_1.v1.adl";
		String xmlFile = "xml/elga/ELGA-Entlassungsbrief_aerztlich_EIS-FullSupport.xml";
		//		adlFile = null;

		if (args != null && args.length > 1) {
			adlFile = args[0];
			xmlFile = args[1];
		}

		AdlToSchematronConverter.run(adlFile, xmlFile);
	}

}

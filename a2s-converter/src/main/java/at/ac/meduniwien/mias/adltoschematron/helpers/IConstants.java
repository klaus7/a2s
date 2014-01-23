package at.ac.meduniwien.mias.adltoschematron.helpers;

/**
 * Some defined constants for the creation of the schematron file.
 * 
 * @author Klaus Pfeiffer
 */
public interface IConstants {

	/**
	 * The namespace of the prefix.
	 */
	@CdaSpecific
	String NAMESPACE = "urn:hl7-org:v3";

	/**
	 * Prefix for the namespace.
	 */
	@CdaSpecific
	String NS_PREFIX = "hl7";

	/**
	 * Prefix with ":".
	 */
	String NS_PREFIX_ = NS_PREFIX + ":";

	/**
	 * Single quote.
	 */
	String IN_QUOTE = "'";

	/**
	 * Double quote.
	 */
	String OUT_QUOTE = "\"";

	/**
	 * Element name of the document root.
	 */
	@CdaSpecific
	String DOCUMENT_ROOT = NS_PREFIX_ + "ClinicalDocument";
}

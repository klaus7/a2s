package at.ac.meduniwien.mias.adltoschematron.helpers;


/**
 * Please refer to the parser.properties for documentation to the configuration.
 * 
 * @author Klaus Pfeiffer
 */
public final class SetProperties {

	/** Hidden constructor. */
	private SetProperties() {
	}

	public static boolean ignoreDuplicateTests;
	public static String[] ignorePatterns;
	public static String adlFolder;
	public static Integer maxNumberArchetypeLevels;

	// TODO not yet implemented
	public static String dateFormat;
	public static Integer testSeverity;

	/**
	 * Whether generated assertions with string comparison should be case sensitive.
	 */
	public static boolean stringComparisonCaseSensitive;
	public static String timeDurationFormat;
	public static String defaultTimeZone;
}

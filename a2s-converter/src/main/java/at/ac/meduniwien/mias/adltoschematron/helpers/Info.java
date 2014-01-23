package at.ac.meduniwien.mias.adltoschematron.helpers;

/**
 * Class holds information about the generated output.
 * 
 * @author Klaus Pfeiffer
 */
public final class Info {

	/**
	 * Hidden constructor.
	 */
	private Info() {
	}

	/**
	 * Number of generated patterns.
	 */
	public static volatile int patterns;

	/**
	 * Number of generated rules.
	 */
	public static volatile int rules;

	/**
	 * Actual generated assertions.
	 */
	public static volatile int assertions;

	/**
	 * Every constraint that triggers the generation of an assertion.
	 * TODO deleteme
	 */
	public static volatile int constraints;

	/**
	 * Skipped elements: attribute already used in context elements.
	 * TODO deleteme
	 */
	public static volatile int skipped;

	/**
	 * Initialize member variables to zero.
	 */
	public static void init() {
		rules = 0;
		patterns = 0;
		assertions = 0;
		constraints = 0;
		skipped = 0;
	}

}

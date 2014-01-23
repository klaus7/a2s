package at.ac.meduniwien.mias.adltoschematron.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import at.ac.meduniwien.mias.adltoschematron.TreeElement;
import at.ac.meduniwien.mias.adltoschematron.enums.ERoleLevel;

/**
 * Information about a test.
 * 
 * @author Klaus
 */
@RequiredArgsConstructor
@Data
@EqualsAndHashCode(of = { "context", "test", "msg" })
public class Test {

	/**
	 * Enumeration of test types.
	 */
	public enum ETestType {
		ExistenceRequired,
		ExistenceOptional,
		ExistenceNotAllowed,
		Cardinality,
		PrimitiveConstraints,
		Occurrences
	}

	/**
	 * Belongs to.
	 */
	private final TreeElement treeElement;

	/**
	 * Type of test.
	 */
	private final ETestType type;

	/**
	 * Rule context.
	 */
	private final String context;

	/**
	 * Error, Warning, Info.
	 */
	private final ERoleLevel role;

	/**
	 * XPath-Test.
	 */
	@NonNull
	private String test;

	/**
	 * Assertion message.
	 */
	private final String msg;

	/**
	 * Test builder flags.
	 * a ... test only as attribute
	 */
	private final String flags;

	/**
	 * The attribute name in the reference model.
	 */
	private final String mainElement;

}

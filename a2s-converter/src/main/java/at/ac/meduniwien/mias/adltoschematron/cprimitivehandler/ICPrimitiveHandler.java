package at.ac.meduniwien.mias.adltoschematron.cprimitivehandler;

/**
 * Interface for handling constraints on primitives.
 * 
 * @author Klaus Pfeiffer
 */
public interface ICPrimitiveHandler {

	/**
	 * @return value of constraint primitive object.
	 */
	String getValue();

	/**
	 * Generate test for tree element.
	 */
	void generateTests();

	/**
	 * @param value
	 * @return wrap value for usage in xpath expression
	 */
	String wrapValue(final Object value);

	/**
	 * @param attr attribute
	 * @return applies transformations on attributes for evaluation in xpath expressions
	 */
	String wrapAttributeFunction(final String attr);

	/**
	 * @param attr attribute
	 * @return applies transformations on attributes for evaluation in xpath expressions
	 */
	String wrapAttributeFunctionLeft(final String attr);

	/**
	 * @param attr attribute
	 * @return applies transformations on attributes for evaluation in xpath expressions
	 */
	String wrapAttributeFunctionRight(final String attr);

}

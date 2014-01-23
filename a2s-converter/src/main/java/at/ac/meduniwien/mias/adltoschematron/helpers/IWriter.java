package at.ac.meduniwien.mias.adltoschematron.helpers;

/**
 * Classes implementing this interface have to provide a write() method for creating a String output of the object.
 * 
 * @author Klaus
 * 
 */
public interface IWriter {
	/** new line. */
	String NEWLINE = "\n";
	/** indent level 1. */
	String INDENT1 = "  ";
	/** indent level 2. */
	String INDENT2 = "    ";
	/** indent level 3. */
	String INDENT3 = "      ";
	/** indent level 4. */
	String INDENT4 = "        ";

	/**
	 * Write out the objects information.
	 * 
	 * @return output
	 */
	String write();
}

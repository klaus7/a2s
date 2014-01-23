package at.ac.meduniwien.mias.adltoschematron.data;

import lombok.Data;
import at.ac.meduniwien.mias.adltoschematron.enums.ERoleLevel;
import at.ac.meduniwien.mias.adltoschematron.helpers.IWriter;
import at.ac.meduniwien.mias.adltoschematron.helpers.Info;
import at.ac.meduniwien.mias.adltoschematron.helpers.Utils;

/**
 * The assertion that gets written out to the schematron file.
 * 
 * @author Klaus Pfeiffer
 */
@Data
public class Assertion implements IWriter {

	/**
	 * The test of this assertion.
	 */
	private final String test;

	/**
	 * Sets the role of this assertion.
	 */
	private final ERoleLevel level;

	/**
	 * Message of this assertion.
	 */
	private final String message;

	@Override
	public String write() {
		Info.assertions++;
		String s;
		s = INDENT3 + "<assert role=" + Utils.oquote(level) + " test=" + Utils.oquote(test) + ">" + message + "</assert>" + NEWLINE;
		return s;
	}
}

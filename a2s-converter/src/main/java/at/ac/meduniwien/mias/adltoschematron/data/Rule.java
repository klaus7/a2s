package at.ac.meduniwien.mias.adltoschematron.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import at.ac.meduniwien.mias.adltoschematron.helpers.IWriter;
import at.ac.meduniwien.mias.adltoschematron.helpers.Info;
import at.ac.meduniwien.mias.adltoschematron.helpers.Utils;

/**
 * Corresponds to a schematron-rule.
 * 
 * @author Klaus
 */
@Data
public class Rule implements IWriter {
	/**
	 * The rule context.
	 */
	private final String context;

	/**
	 * The list of assertions the rule has.
	 */
	private List<Assertion> assertions = new ArrayList<Assertion>();

	/**
	 * Comments written above the rule.
	 */
	private final String comments;

	@Override
	public String write() {
		Info.rules++;
		String s = "";
		s = INDENT2 + "<!-- " + comments + " -->" + NEWLINE;
		s += INDENT2 + "<rule context=" + Utils.oquote(context) + ">" + NEWLINE;
		for (Assertion a : assertions) {
			s += a.write();
		}
		s += INDENT2 + "</rule>" + NEWLINE;
		return s;
	}
}

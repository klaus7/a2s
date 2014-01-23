package at.ac.meduniwien.mias.adltoschematron.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import at.ac.meduniwien.mias.adltoschematron.helpers.IWriter;
import at.ac.meduniwien.mias.adltoschematron.helpers.Info;
import at.ac.meduniwien.mias.adltoschematron.helpers.Utils;

/**
 * The actual pattern that gets written out to the schematron file.
 * 
 * @author Klaus Pfeiffer
 */
@Data
public class Pattern implements IWriter {

	/**
	 * Name of the pattern.
	 */
	private final String name;

	/**
	 * Rules of this pattern.
	 */
	private List<Rule> rules = new ArrayList<Rule>();

	@Override
	public String write() {
		Info.patterns++;
		String s = "";
		//		s += INDENT1 + "<!-- " + comments + " -->";
		s += INDENT1 + "<pattern id=" + Utils.oquote(name) + ">" + NEWLINE;
		for (Rule r : rules) {
			s += r.write();
		}
		s += INDENT1 + "</pattern>" + NEWLINE;
		return s;
	}
}

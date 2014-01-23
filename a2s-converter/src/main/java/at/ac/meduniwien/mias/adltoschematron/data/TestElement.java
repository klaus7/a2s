package at.ac.meduniwien.mias.adltoschematron.data;

import java.util.List;

import lombok.Data;
import at.ac.meduniwien.mias.adltoschematron.helpers.IConstants;
import at.ac.meduniwien.mias.adltoschematron.helpers.IWriter;

/**
 * Helps to build a test with checks on attributes with values.
 * 
 * @author Klaus
 */
@Data
public class TestElement implements IWriter, IConstants {
	/**
	 * Attributes that get concatenated with an operator.
	 */
	private List<Attribute> attributes;

	/**
	 * and, or, etc.
	 */
	private String operator;

	@Override
	public String write() {
		StringBuilder s = new StringBuilder();

		if (attributes != null) {

			for (Attribute a : attributes) {
				s.append(a.write());
				s.append(" " + operator + " ");
			}
			// remove last logical operator
			s.delete(s.length() - operator.length() - 2, s.length());
		}
		return s.toString();
	}

}

package at.ac.meduniwien.mias.adltoschematron.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import at.ac.meduniwien.mias.adltoschematron.helpers.IConstants;
import at.ac.meduniwien.mias.adltoschematron.helpers.IWriter;
import at.ac.meduniwien.mias.adltoschematron.helpers.Utils;

/**
 * @author Klaus
 */
@Data
@AllArgsConstructor
public class Attribute implements IWriter, IConstants {
	/**
	 * Attribute name.
	 */
	private String name;

	/**
	 * The test. e.g. "=123"
	 */
	private String test;

	@Override
	public String write() {
		StringBuilder s = new StringBuilder();
		if (name.startsWith(NS_PREFIX_)) {
			s.append(Utils.attributeWrapper(name.substring(NS_PREFIX_.length(), name.length())));
		} else {
			s.append(Utils.attributeWrapper(name));
		}
		s.append(test);
		return s.toString();
	}

}

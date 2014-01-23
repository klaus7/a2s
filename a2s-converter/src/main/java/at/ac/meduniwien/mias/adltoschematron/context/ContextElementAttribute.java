package at.ac.meduniwien.mias.adltoschematron.context;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Defines the name of the context element and the value gets set during the generation of the TreeElement structure.
 * 
 * @author Klaus Pfeiffer
 */
@Data
public class ContextElementAttribute {
	/**
	 * name of xml attribute.
	 */
	private String name;

	/**
	 * value of xml attribute. (do not configure!)
	 */
	private String value;

	/**
	 * priority-list of values.
	 */
	private List<String> priorityItems;

	/**
	 * copy constructor.
	 * 
	 * @param copy object to copy
	 */
	public ContextElementAttribute(final ContextElementAttribute copy) {
		name = copy.name;
		value = copy.value;
		priorityItems = new ArrayList<String>(copy.getPriorityItems());
	}

	/**
	 * default constructor.
	 */
	public ContextElementAttribute() {
		priorityItems = new ArrayList<String>();
	}

}

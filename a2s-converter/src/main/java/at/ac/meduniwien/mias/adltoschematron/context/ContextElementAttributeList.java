package at.ac.meduniwien.mias.adltoschematron.context;

import java.util.ArrayList;

/**
 * Provide convenience methods for the List.
 * 
 * @author Klaus Pfeiffer
 */
public class ContextElementAttributeList extends ArrayList<ContextElementAttribute> {

	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public ContextElementAttributeList() {
		super();
	}

	/**
	 * Copy constructor.
	 * 
	 * @param copy object to copy
	 */
	public ContextElementAttributeList(final ContextElementAttributeList copy) {
		super();
		for (ContextElementAttribute cea : copy) {
			add(new ContextElementAttribute(cea));
		}
	}

	/**
	 * @param name name of {@link ContextElementAttribute}
	 * @return ContextElementAttribute with attribute name set to argument name
	 */
	public ContextElementAttribute getByName(final String name) {
		for (ContextElementAttribute cea : this) {
			if (cea.getName().equals(name)) {
				return cea;
			}
		}
		return null;
	}

	/**
	 * @param value value of {@link ContextElementAttribute}
	 * @return ContextElementAttribute with attribute value set to argument value
	 */
	public ContextElementAttribute getByValue(final String value) {
		for (ContextElementAttribute cea : this) {
			if (cea.getValue().equals(value)) {
				return cea;
			}
		}
		return null;
	}

}

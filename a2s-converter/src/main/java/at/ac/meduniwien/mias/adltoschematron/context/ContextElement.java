package at.ac.meduniwien.mias.adltoschematron.context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.Data;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import at.ac.meduniwien.mias.adltoschematron.helpers.Utils;

/**
 * Element that represents a context for the generation of Schematron rules.
 * 
 * @author Klaus Pfeiffer
 */
@Data
public class ContextElement {

	/**
	 * the level in the tree. (do not configure!)
	 */
	private int level = 0;

	/**
	 * name of the xml element.
	 */
	private String element;

	/**
	 * list of xml attributes.
	 */
	private ContextElementAttributeList attributes;

	/**
	 * custom strategy.
	 */
	private IContextElementStrategy strategy = null;

	/**
	 * if the context is only built when inside of the scope of the element. <br/>
	 * otherwise also on the level of the context element.
	 */
	private boolean contextForChildLevel = false;

	/**
	 * child elements.
	 */
	private List<ContextElement> children;

	/**
	 * default constructor.
	 */
	public ContextElement() {
	}

	/**
	 * @param copy
	 *        deep copy this context element into a new one.
	 */
	public ContextElement(final ContextElement copy) {
		level = copy.level;
		element = new String(copy.element);
		attributes = new ContextElementAttributeList(copy.attributes);
		contextForChildLevel = copy.contextForChildLevel;

		if (copy.children != null) {
			// deep copy children
			children = new ArrayList<ContextElement>(copy.children.size());
			for (ContextElement ce : copy.children) {
				children.add(new ContextElement(ce));
			}
		}
	}

	/**
	 * @return main context of context element.
	 */
	public String generateContext() {
		String sc = "";
		ContextElement ce = this;
		Iterator<ContextElementAttribute> attributeIter = ce.getAttributes().iterator();
		String attribContext = "";
		while (attributeIter.hasNext()) {
			ContextElementAttribute ceAttribute = attributeIter.next();
			attribContext += "@" + ceAttribute.getName() + "=" + Utils.quote(ceAttribute.getValue());
			if (attributeIter.hasNext()) {
				attribContext += " and ";
			}
		}

		sc += "[" + Utils.NS_PREFIX + ":" + ce.getElement() + "[" + attribContext + "]]";
		return sc;
	}

	/**
	 * Generate context tree with all specified children.
	 * 
	 * @param ce context element
	 * @param first if this is the first child
	 * @return child context of context element.
	 */
	public String generateSecondContextTree(final ContextElement ce, final boolean first) {
		String sc = "";

		Iterator<ContextElementAttribute> attributeIter = ce.getAttributes().iterator();
		String attribContext = "";

		while (attributeIter.hasNext()) {

			ContextElementAttribute ceAttribute = attributeIter.next();
			if (ceAttribute.getValue() != null) {

				attribContext += "@" + ceAttribute.getName() + "=" + Utils.quote(ceAttribute.getValue());
				if (attributeIter.hasNext()) {
					attribContext += " and ";
				}
			}
		}
		if (!StringUtils.isEmpty(attribContext)) {
			sc += Utils.NS_PREFIX + ":" + ce.getElement() + "[" + attribContext + "]";
		} else {
			// cancel on this level
			return "";
		}

		if (!CollectionUtils.isEmpty(ce.getChildren())) {
			for (ContextElement child : ce.getChildren()) {
				sc += generateSecondContextTree(child, false);
			}
		}

		// correct nesting, so we can have as much children of children in the context elements as we want
		if (first) {
			sc = "/" + sc;
		} else {
			sc = "[" + sc + "]";
		}

		return sc;
	}
}

package at.ac.meduniwien.mias.adltoschematron;

import static at.ac.meduniwien.mias.adltoschematron.helpers.Utils.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openehr.am.archetype.constraintmodel.CAttribute;
import org.openehr.am.archetype.constraintmodel.CComplexObject;
import org.openehr.am.archetype.constraintmodel.CPrimitiveObject;

import at.ac.meduniwien.mias.adltoschematron.context.ContextElement;
import at.ac.meduniwien.mias.adltoschematron.context.ContextElementAttribute;
import at.ac.meduniwien.mias.adltoschematron.cprimitivehandler.ICPrimitiveHandler;
import at.ac.meduniwien.mias.adltoschematron.cprimitivehandler.TestGenerator;
import at.ac.meduniwien.mias.adltoschematron.helpers.IConstants;
import at.ac.meduniwien.mias.adltoschematron.helpers.IWriter;
import at.ac.meduniwien.mias.adltoschematron.helpers.Utils;

/**
 * Represents an element in the archetype-tree and offers additional methods to navigate in the tree and create context
 * and tests.
 * 
 * @author Klaus Pfeiffer
 */
public class TreeElement {

	/** declared to improve readability of code. */
	public static final boolean FROM_CA = true;

	/** declared to improve readability of code. */
	public static final boolean FROM_CCO = false;

	/** Parent element. */
	@Getter
	@Setter
	private TreeElement parent;

	/** Holds every child of this tree element. */
	@Getter
	private final List<TreeElement> children;

	/** The constraint specified on this element. */
	@Getter
	@Setter
	private CAttribute ca;

	/** The constraint specified on this element. */
	@Getter
	@Setter
	private CComplexObject cco;

	/** The constraint specified on this element. */
	@Getter
	@Setter
	private CPrimitiveObject cpo;

	/** The context in which this tree element is. */
	@Getter
	private final Deque<ContextElement> contextElements;

	/** Level in tree of current element. */
	@Getter
	@Setter
	private int level;

	/**
	 * Default constructor.
	 * 
	 * @param parent
	 *        parent tree element
	 * @param ca CAttribute
	 * @param cco CComplexObject
	 * @param level
	 *        level of current element
	 */
	public TreeElement(final TreeElement parent, final CAttribute ca, final CComplexObject cco, final int level) {
		children = (new ArrayList<TreeElement>());
		setParent(parent);
		setCco(cco);
		setCa(ca);
		this.level = level;
		contextElements = (new ArrayDeque<ContextElement>());
		if (getParent() != null) {
			getParent().getChildren().add(this);
		}
	}

	/**
	 * Default constructor.
	 * 
	 * @param parent
	 *        parent tree element
	 * @param ca CAttribute
	 * @param cpo CPrimitiveObject
	 * @param level
	 *        level of current element
	 */
	public TreeElement(final TreeElement parent, final CAttribute ca, final CPrimitiveObject cpo, final int level) {
		children = (new ArrayList<TreeElement>());
		setParent(parent);
		setCpo(cpo);
		setCa(ca);
		this.level = level;
		contextElements = (new ArrayDeque<ContextElement>());
		if (getParent() != null) {
			getParent().getChildren().add(this);
		}
	}

	// RETRIEVE CONTEXT

	/**
	 * @return list of neighbors of this tree element.
	 */
	public List<TreeElement> getNeighbors() {
		List<TreeElement> neighbors = new ArrayList<TreeElement>();

		for (TreeElement treeElement : getParent().getChildren()) {
			if (treeElement != this) {
				neighbors.add(treeElement);
			}
		}

		return neighbors;
	}

	/**
	 * Fill contextElements member variable with proper input of constraint tree.
	 */
	public void processContextElements() {
		final TreeElement thisTreeElement = this;

		/*if (thisTreeElement.hasCco()) */{

			// main context elements
			TreeElement treeElementParent = thisTreeElement.getParent();

			if (treeElementParent != null) {

				// find context element in parent's children
				// --> is context giving element on same level as this tree element?

				for (TreeElement parentsChild : treeElementParent.getChildren()) {
					final ContextElement found = treeElementMatchesInFixedContextElements(parentsChild);

					if (found != null && !found.isContextForChildLevel()/* && parentsChild.getCco().equals(thisTreeElement.getCco())*/) {

						ContextElement latest = thisTreeElement.getContextElements().peek();

						if (found != null && (latest == null || !latest.getElement().equals(found.getElement()))) {
							// fill with values
							ContextElement valueElement = retrieveValuesFromTreeElement(parentsChild, found);
							if (valueElement != null) {

								thisTreeElement.getContextElements().push(valueElement);

							}
						}
					}
				}
			}

			// child context elements
			{
				ContextElement found = treeElementMatchesInFixedContextElements(thisTreeElement);

				if (found != null && found.isContextForChildLevel()) {

					ContextElement valueElement = retrieveValuesFromTreeElement(thisTreeElement, found);

					if (valueElement != null) {

						thisTreeElement.getContextElements().push((valueElement));

					}
				}
			}

		}
		return;
	}

	/**
	 * Retrieves values and starts setting values into the context element.
	 * 
	 * @param treeElement tree element to retrieve values from
	 * @param foundContextElement found context element
	 * @return context element filled with values
	 */
	private ContextElement retrieveValuesFromTreeElement(final TreeElement treeElement,
			final ContextElement foundContextElement) {

		if (treeElement.hasCa()) {

			// copy context element from fixed context elements
			ContextElement ce = new ContextElement(foundContextElement);
			TreeElement bestMatchingElement = getBestMatchingTreeElement(treeElement, foundContextElement);
			boolean isnull = true;

			if (bestMatchingElement != null) {

				for (ContextElementAttribute attr : ce.getAttributes()) {
					attr.setValue(getAttributeValueFromTreeElement(attr, bestMatchingElement));
					if (attr.getValue() != null) {
						isnull = false;
					}
				}
			}

			setValueInContextElementChildren(bestMatchingElement, ce);

			// if e.g. a templateId with no assignedValue is specified, it may not be used for creating a context
			if (isnull) {
				return null;
			}

			return ce;

		}

		return null;

	}

	/**
	 * Recursivly sets all values in the whole context element structure.
	 * 
	 * @param ccoTreeElement current tree element with cco
	 * @param ce context element to set value in
	 * @return context element filled with values
	 */
	private ContextElement setValueInContextElementChildren(final TreeElement ccoTreeElement, final ContextElement ce) {
		if (ce.getChildren() != null) {
			for (ContextElement child : ce.getChildren()) {
				TreeElement childTreeElement = findCcoChildTreeElement(ccoTreeElement, child);
				for (ContextElementAttribute attr : child.getAttributes()) {
					if (ccoTreeElement != null) {
						attr.setValue(getAttributeValueFromTreeElement(attr, childTreeElement));
					}
				}
				setValueInContextElementChildren(findCcoChildTreeElement(childTreeElement, child), child);
			}
		}
		return ce;
	}

	/**
	 * 
	 * @param ccoTreeElement look in its children if the reference model name equals the element name of the context element.
	 * @param child context element
	 * @return found tree element, or null
	 */
	private TreeElement findCcoChildTreeElement(final TreeElement ccoTreeElement, final ContextElement child) {
		if (ccoTreeElement != null && ccoTreeElement.getChildren() != null) {
			for (TreeElement treeChild : ccoTreeElement.getChildren()) {
				if (treeChild.getRmName(FROM_CA).equals(child.getElement())) {
					return treeChild;
				}
			}
		}
		return null;
	}

	/**
	 * When various CCO are possible (e.g. in case of templateIds) then try to find the best matching.
	 * For now only rules applying to the templateId are implemented.
	 * 
	 * @param treeElement current tree element
	 * @param foundContextElement the found context element
	 * @return best matching cco, or ccoTreeElement if none was found
	 */
	private TreeElement getBestMatchingTreeElement(final TreeElement treeElement,
			final ContextElement foundContextElement) {

		if (foundContextElement.getStrategy() != null
			&& treeElement.getRmName(FROM_CA).equalsIgnoreCase(foundContextElement.getElement())) {

			return foundContextElement.getStrategy().getBestMatchingTreeElementStrategy(foundContextElement,
					treeElement);
		} else {

			// no other rules, just return argument:
			return treeElement;
		}
	}

	/**
	 * Searches for attribute name in tree element and retrieves its value.
	 * 
	 * @param attr attribute to get name from
	 * @param ccoElement the children of this cco get searched
	 * @return attribute value
	 */
	private String getAttributeValueFromTreeElement(final ContextElementAttribute attr, final TreeElement ccoElement) {

		if (ccoElement != null && !CollectionUtils.isEmpty(ccoElement.getChildren())) {

			for (TreeElement caElement : ccoElement.getChildren()) {

				if (caElement.getRmName(FROM_CA).equalsIgnoreCase(attr.getName())) {

					ICPrimitiveHandler handler = TestGenerator.getCPrimitiveHandlerForTreeElement(caElement);

					return handler.getValue();

				}
			}
		}

		return null;

	}

	/**
	 * @param treeElement look in tree element for configured context elements
	 * @return found context element or null
	 */
	private ContextElement treeElementMatchesInFixedContextElements(final TreeElement treeElement) {

		if (treeElement.hasCa()) {

			for (ContextElement fixedContextElement : AdlToSchematronConverter.fixedContextElements) {

				// match element name
				if (treeElement.getCa().getRmAttributeName().equals(fixedContextElement.getElement())) {

					return fixedContextElement;

				}
			}

		}

		return null;
	}

	// private boolean childrenMatch(final TreeElement element, final ContextElement celement) {
	// if (!CollectionUtils.isEmpty(celement.getChildren())) {
	// boolean childMatch = true;
	// for (ContextElement child : celement.getChildren()) {
	// if (childrenMatch(element, child));
	// }
	// }
	// if (element.getCA().getRmAttributeName().equals(celement.getElement())) {
	// }
	// }

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		sb.append(super.toString());
		sb.append(IWriter.NEWLINE);

		if (hasCa()) {
			sb.append("CA: " + getCa().getRmAttributeName() + IWriter.NEWLINE);
			sb.append(getCa().path() + IWriter.NEWLINE);
		}

		if (hasCco()) {
			sb.append("CCO: " + getCco().getRmTypeName() + IWriter.NEWLINE);
			sb.append(getCco().path() + IWriter.NEWLINE);
		}

		if (hasCpo()) {
			sb.append("CPO: " + getCpo().getRmTypeName() + IWriter.NEWLINE);
			if (getCpo().hasAssignedValue()) {
				sb.append("value: " + getCpo().getItem().assignedValue());
			}
			sb.append(getCpo().path() + IWriter.NEWLINE);
		}

		return sb.toString();
	}

	/**
	 * Provided following situation, this method returns
	 * 
	 * <pre>
	 * //*[hl7:templateId[@assigningAuthorityName='ELGA' and @root='1.2']]/hl7:templateId
	 * </pre>
	 * 
	 * if called on the tree element describing the templateId and II objects.
	 * 
	 * <pre>
	 * ClinicalDocument[at0000] matches {
	 * 	ca existence matches {1..1} matches {
	 * 		CCO2 occurrences matches {2..3} matches {
	 * 			templateId existence matches {1..1} cardinality matches {1..*; unordered; unique} matches {
	 * 				II occurrences matches {0..*} matches {  -- II
	 * 					root existence matches {0..1} matches {"1.3"}
	 * 					assigningAuthorityName existence matches {0..1} matches {"ELGA"}
	 * 				}
	 * 			}
	 * 			string_attr matches {"anything"}
	 * 		}
	 * 	}
	 * }
	 * </pre>
	 * 
	 * @return context of tree element. If current element does not have a context, the context is searched recursively
	 *         in its parents.
	 */
	public String getContext() {

		if (!CollectionUtils.isEmpty(contextElements)) {

			// add main context
			String mainContext = "";
			for (ContextElement ce : contextElements) {

				if (!ce.isContextForChildLevel()) {

					mainContext += ce.generateContext();

				}

			}

			// add child context
			String childContext = "";
			for (ContextElement ce : contextElements) {

				if (ce.isContextForChildLevel()) {

					childContext += ce.generateSecondContextTree(ce, true);

				}

			}

			// add own context
			String ownContext = "";
			if (StringUtils.isEmpty(childContext)) {

				ownContext = addAttributeToContext(this);

			}

			return "//*" + mainContext + childContext + ownContext;

		} else {

			// add elements
			String sc = "";

			sc += addAttributeToContext(this);

			if (getParent() == null) {

				return sc;

			} else {

				return getParent().getContext() + sc;

			}
		}
	}

	/**
	 * Provided following situation, this method returns
	 * 
	 * <pre>
	 * //*[hl7:templateId[@assigningAuthorityName='ELGA' and @root='1.2']]
	 * </pre>
	 * 
	 * if called on the tree element describing the templateId and II objects.
	 * 
	 * <pre>
	 * ClinicalDocument[at0000] matches {
	 * 	ca existence matches {1..1} matches {
	 * 		CCO2 occurrences matches {2..3} matches {
	 * 			templateId existence matches {1..1} cardinality matches {1..*; unordered; unique} matches {
	 * 				II occurrences matches {0..*} matches {  -- II
	 * 					root existence matches {0..1} matches {"1.3"}
	 * 					assigningAuthorityName existence matches {0..1} matches {"ELGA"}
	 * 				}
	 * 			}
	 * 			string_attr matches {"anything"}
	 * 		}
	 * 	}
	 * }
	 * </pre>
	 * 
	 * @return context of tree element. If current element does not have a context, the context is searched recursively
	 *         in its parents.
	 */
	public String getParentContext() {

		if (!CollectionUtils.isEmpty(contextElements)) {

			// add main context
			String mainContext = "";
			for (ContextElement ce : contextElements) {

				if (!ce.isContextForChildLevel()) {

					mainContext += ce.generateContext();

				}

			}

			// add child context
			String childContext = "";
			for (ContextElement ce : contextElements) {

				if (ce.isContextForChildLevel()) {

					childContext += ce.generateSecondContextTree(ce, true);

				}

			}
			if (!StringUtils.isEmpty(childContext)) {
				childContext = "[" + childContext + "]";
			}

			return "//*" + mainContext + childContext;

		} else {

			// add elements
			String sc = "";

			if (getParent() == null) {

				return sc;

			} else {

				return getParent().getContext() + sc;

			}
		}
	}

	/**
	 * @param te
	 *        tree element
	 * @return context string with added attribute if applicable
	 */
	private String addAttributeToContext(final TreeElement te) {
		String s = "";

		// xml attributes do not provide context, they get tested
		if (te != null && te.hasCa() && !te.isXmlAttribute()) {
			s = "/" + te.getName(FROM_CA);
		} else if (te != null && te.getParent() == null) {
			s = "/" + IConstants.DOCUMENT_ROOT;
		}

		return s;
	}

	/**
	 * Get all XML attributes from a CCO.
	 * 
	 * @return tree elements that are xml attributes, null if none
	 */
	public List<TreeElement> getXmlAttributes() {
		if (hasCco()) {
			List<TreeElement> attr = new ArrayList<TreeElement>();
			for (TreeElement e : children) {
				if (e.isXmlAttribute()) {
					attr.add(e);
				}
			}
			return attr;
		} else {
			return null;
		}
	}

	/**
	 * @return context templateId. If null, the context templateId from the parent is retrieved.
	 */
	public Deque<ContextElement> getInheritedContextElements() {

		if (contextElements != null && !contextElements.isEmpty()) {

			return contextElements;

		} else {

			if (getParent() == null) {
				// element does not have specific context
				return contextElements;
			}

			// add elements
			return getParent().getInheritedContextElements();

		}

	}

	/**
	 * @return "attribute" if xml-attribute, "element" otherwise
	 */
	public String getMsgPrefix() {
		return (isXmlAttribute() ? lang("attribute") : lang("element")) + WHITESPACE;
	}

	/**
	 * Check if attribute name was already used in context elements.
	 * 
	 * @param contextElements context elements
	 * @param rmAttributename attribute name
	 * @return true if we found the attribute name in the context elements, false otherwise
	 */
	public boolean isAttributeInContextElements(final Collection<ContextElement> contextElements,
			final String rmAttributename) {
		if (!CollectionUtils.isEmpty(contextElements)) {
			for (ContextElement ce : contextElements) {
				for (ContextElementAttribute cea : ce.getAttributes()) {
					if (cea.getName().equals(rmAttributename)) {
						return true;
					}
				}
			}
			for (ContextElement ce : contextElements) {
				if (isAttributeInContextElements(ce.getChildren(), rmAttributename)) {
					return true;
				}
			}
			return false;
		}
		if (getParent() != null) {
			return getParent().isAttributeInContextElements(getParent().getContextElements(), rmAttributename);
		}
		return false;
	}

	/**
	 * @param fromCa wheter the name of the CA is asked
	 * @return rm-name or derived name
	 */
	public String getName(final boolean fromCa) {
		if (fromCa) {
			if (isXmlAttribute()) {
				return getCa().getRmAttributeName();
			}
			if (hasCa()) {
				return Utils.NS_PREFIX_ + getCa().getRmAttributeName();
			}
		} else {
			if (hasCco()) {
				return Utils.NS_PREFIX_ + getCco().getRmTypeName();
			}
			if (hasCpo()) {
				return getCpo().getRmTypeName();
			}
		}
		return null;
	}

	/**
	 * @param fromCa
	 *        retrieve vom attribute constraint or from complex object constraint (resp. primitive object
	 *        constraint)
	 * @return reference model name
	 */
	public String getRmName(final boolean fromCa) {
		if (fromCa && hasCa()) {
			return getCa().getRmAttributeName();
		}
		if (hasCco()) {
			return getCco().getRmTypeName();
		}
		if (hasCpo()) {
			return getCpo().getRmTypeName();
		}
		return null;
	}

	/**
	 * @return is current element is an XML attribute.
	 */
	public boolean isXmlAttribute() {
		return hasCa() && hasCpo();
	}

	/**
	 * @return true, if element is of type CComplexObject
	 */
	public boolean hasCco() {
		return cco != null;
	}

	/**
	 * @return true, if element is of type CAttribute
	 */
	public boolean hasCa() {
		return ca != null;
	}

	/**
	 * @return true, if element is of type CPrimitiveObject
	 */
	public boolean hasCpo() {
		return cpo != null;
	}

}

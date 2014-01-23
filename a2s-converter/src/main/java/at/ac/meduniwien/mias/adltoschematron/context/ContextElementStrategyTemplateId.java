package at.ac.meduniwien.mias.adltoschematron.context;

import at.ac.meduniwien.mias.adltoschematron.TreeElement;
import at.ac.meduniwien.mias.adltoschematron.helpers.CdaSpecific;
import at.ac.meduniwien.mias.adltoschematron.helpers.Utils;

/**
 * The strategy for finding the correct template id.
 * 
 * @author Klaus Pfeiffer
 */
@CdaSpecific
public class ContextElementStrategyTemplateId implements IContextElementStrategy {

	@Override
	public TreeElement getBestMatchingTreeElementStrategy(final ContextElement foundContextElement, final TreeElement treeElement) {

		TreeElement currentBestMatching = null;

		// loop through priority items for the templateId configured in contextElements.xml
		for (String assigningAuthorityName : foundContextElement.getAttributes().getByName("assigningAuthorityName").getPriorityItems()) {

			// rules for best matching templateId:
			int max = 0;
			ccoElements: for (TreeElement treeElementFromParentsChildren : treeElement.getParent().getChildren()) {
				if (treeElementFromParentsChildren.getRmName(TreeElement.FROM_CA).equalsIgnoreCase(foundContextElement.getElement())) {

					// we are inside the xml element here
					/// iterations over children mean we iterate over the possible values of the attributes 
					/// or nested xml elements

					// search for constraint on attribute assigningAuthorityName
					for (TreeElement treeElementXmlAttributeChild : treeElementFromParentsChildren.getChildren()) {
						if (treeElementXmlAttributeChild.getRmName(TreeElement.FROM_CA).equalsIgnoreCase("assigningAuthorityName")) {
							final String value = (String) treeElementXmlAttributeChild.getCpo().getItem().assignedValue();
							if (!assigningAuthorityName.equalsIgnoreCase(value)) {
								// if not current priority item, take next CCO
								continue ccoElements;
							}
						}
					}
					// search for constraint on attribute root
					for (TreeElement treeElementXmlAttributeChild : treeElementFromParentsChildren.getChildren()) {
						if (treeElementXmlAttributeChild.getRmName(TreeElement.FROM_CA).equalsIgnoreCase("root")) {
							final String value = (String) treeElementXmlAttributeChild.getCpo().getItem().assignedValue();
							if (value != null) {
								int spec = Utils.getInstanceIdentifierSpecificity(value);
								if (spec > max) {
									max = spec;
									currentBestMatching = treeElementFromParentsChildren;
								}
							}
						}
					}
				}
			}

			if (currentBestMatching != null) {
				break;
			}
		}

		return currentBestMatching;
	}

}

package at.ac.meduniwien.mias.adltoschematron.cprimitivehandler;

import static at.ac.meduniwien.mias.adltoschematron.helpers.Utils.*;

import java.util.List;

import lombok.extern.log4j.Log4j;

import org.apache.commons.lang.StringUtils;
import org.openehr.am.archetype.constraintmodel.CAttribute;
import org.openehr.am.archetype.constraintmodel.CAttribute.Existence;
import org.openehr.am.archetype.constraintmodel.CMultipleAttribute;
import org.openehr.am.archetype.constraintmodel.CObject;
import org.openehr.am.archetype.constraintmodel.CSingleAttribute;

import at.ac.meduniwien.mias.adltoschematron.TreeElement;
import at.ac.meduniwien.mias.adltoschematron.data.Test.ETestType;
import at.ac.meduniwien.mias.adltoschematron.helpers.IConstants;
import at.ac.meduniwien.mias.adltoschematron.helpers.Utils;

@Log4j
public class CAttributeHandler extends AbstractCPrimitiveHandler {

	public CAttributeHandler(final TreeElement te) {
		super(te);
	}

	@Override
	public String getValue() {
		throw new UnsupportedOperationException("getValue() not supported on CAttributeHandler");
	}

	@Override
	public String wrapValue(final Object value) {
		throw new UnsupportedOperationException("wrapValue(Object) not supported on CAttributeHandler");
	}

	@Override
	public void generateTests() {
		TreeElement te = getTreeElement();
		CAttribute a = te.getCa();

		if (a instanceof CSingleAttribute) {

			CSingleAttribute csa = (CSingleAttribute) a;

			List<CObject> l1 = csa.alternatives();
			List<CObject> l2 = csa.getChildren();

			if (!l1.equals(l2)) {
				log.error("alternatives-list not equal to children!");
			}

		} else if (a instanceof CMultipleAttribute) {

			new CMultipleAttributeHandler(te).generateTests();
		}

		// process existence
		Existence existence = a.getExistence();

		if (existence.equals(Existence.REQUIRED) || existence.equals(Existence.NOT_ALLOWED)) {

			// check if we should skip test generation
			//			for (ContextElement ce : te.getContextElements()) {
			//				if (ce.getElement().equals(te.getRmName(true))) {
			//					log.debug("Element " + ce.getElement() + " already found in context element. Skip 'required' test generation. ");
			//					return;
			//				}
			//			}

			String context;
			String test;
			String lang;

			if (existence.equals(Existence.REQUIRED)) {
				lang = "isRequired";
			} else {
				lang = "isNotAllowed";
			}

			if (te.isXmlAttribute()) {

				// the data could also be in the xml element (and not in the xml attribute). 
				/// for this case, all tests with an attribute in it are rewritten later on in the generation process

				context = te.getContext();
				test = "@" + te.getRmName(true);

			} else {
				if (te.getParent() != null) {

					context = te.getParentContext();
					if (StringUtils.isEmpty(context)) {
						context = "/" + IConstants.DOCUMENT_ROOT;
					}
					test = Utils.NS_PREFIX_ + te.getRmName(true);

				} else {
					context = "/";
					test = Utils.NS_PREFIX_ + te.getRmName(true);
				}

			}

			addTest(
					ETestType.ExistenceRequired,
					context,
					test,
					te.getMsgPrefix() + lang(lang, quote(a.getRmAttributeName())), a.getRmAttributeName());

		} else if (existence.equals(Existence.OPTIONAL)) {

			// no optional assertions created until now

		}

	}

}

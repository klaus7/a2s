package at.ac.meduniwien.mias.adltoschematron.cprimitivehandler;

import static at.ac.meduniwien.mias.adltoschematron.helpers.Utils.*;

import java.util.List;

import lombok.extern.log4j.Log4j;

import org.openehr.am.archetype.constraintmodel.CAttribute;
import org.openehr.am.archetype.constraintmodel.CMultipleAttribute;
import org.openehr.am.archetype.constraintmodel.CObject;
import org.openehr.am.archetype.constraintmodel.Cardinality;
import org.openehr.rm.support.basic.Interval;

import at.ac.meduniwien.mias.adltoschematron.TreeElement;
import at.ac.meduniwien.mias.adltoschematron.data.Test.ETestType;
import at.ac.meduniwien.mias.adltoschematron.helpers.IConstants;
import at.ac.meduniwien.mias.adltoschematron.helpers.Utils;

@Log4j
public class CMultipleAttributeHandler extends AbstractCPrimitiveHandler {

	public CMultipleAttributeHandler(final TreeElement te) {
		super(te);
	}

	@Override
	public String wrapValue(final Object value) {
		return value.toString();
	}

	@Override
	public void generateTests() {
		TreeElement te = getTreeElement();
		CAttribute a = te.getCa();
		setRmAttributeName(Utils.attributeWrapper(a.getRmAttributeName()));

		CMultipleAttribute cma = (CMultipleAttribute) a;

		List<CObject> l1 = cma.members();
		List<CObject> l2 = cma.getChildren();

		if (!l1.equals(l2)) {
			log.error("members-list not equal to children!");
		}

		Cardinality c = cma.getCardinality();
		Interval<Integer> interval = c.getInterval();

		if (interval != null) {
			String testPrefix = "";
			String testPostfix = "";

			if (te.hasCa()) {
				// only when element is present, we can validate the cardinality
				testPrefix = "not(" + Utils.NS_PREFIX_ + te.getRmName(true) + ") or (";
				testPostfix = ")";
			}

			String context = te.getParentContext();
			if (interval.getLower() != null) {
				if (!interval.isLowerIncluded() || (interval.isLowerIncluded() && interval.getLower() > 0)) {
					addTest(ETestType.Cardinality,
							context,
							testPrefix + TestGenerator.createIntervalCheckLowerBoundary(this, interval, getRmAttributeName()) + testPostfix,
							lang("CACardinalityLowerBoundary", quote(getRmAttributeName()), wrapValue(interval.getLower())),
							getRmAttributeName());
				}
			}
			if (interval.getUpper() != null) {
				addTest(ETestType.Cardinality,
						context,
						testPrefix + TestGenerator.createIntervalCheckUpperBoundary(this, interval, getRmAttributeName()) + testPostfix,
						lang("CACardinalityUpperBoundary", quote(getRmAttributeName()), wrapValue(interval.getUpper())),
						getRmAttributeName());
			}
		}
	}

	@Override
	public String wrapAttributeFunctionLeft(final String attr) {

		// adds function "count" to attribute and removes xml attribute symbol,
		/// because item count cardinality can never occur on xml attributes

		return "fn:count(" + IConstants.NS_PREFIX_ + (attr.replaceAll("@", "")) + ")";
	}

}

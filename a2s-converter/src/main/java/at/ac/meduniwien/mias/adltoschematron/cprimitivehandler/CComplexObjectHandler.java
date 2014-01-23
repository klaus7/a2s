package at.ac.meduniwien.mias.adltoschematron.cprimitivehandler;

import static at.ac.meduniwien.mias.adltoschematron.helpers.Utils.*;

import org.apache.commons.lang.StringUtils;
import org.openehr.am.archetype.constraintmodel.CComplexObject;
import org.openehr.rm.support.basic.Interval;

import at.ac.meduniwien.mias.adltoschematron.TreeElement;
import at.ac.meduniwien.mias.adltoschematron.data.Test;
import at.ac.meduniwien.mias.adltoschematron.data.Test.ETestType;
import at.ac.meduniwien.mias.adltoschematron.enums.ERoleLevel;
import at.ac.meduniwien.mias.adltoschematron.helpers.Utils;

public class CComplexObjectHandler extends AbstractCPrimitiveHandler {

	public CComplexObjectHandler(final TreeElement te) {
		super(te);
	}

	@Override
	public String wrapValue(final Object value) {
		return value.toString();
	}

	@Override
	public String wrapAttributeFunction(final String attr) {
		// remove @ symbol
		return super.wrapAttributeFunction(attr).replaceAll("@", "");
	}

	@Override
	public void generateTests() {
		TreeElement te = getTreeElement();
		CComplexObject cco = getTreeElement().getCco();
		Interval<Integer> interval = cco.getOccurrences();

		/*
		 * Get context from children, but only if we are not the root node and the children have have context elements.
		 */
		if (te.getChildren().size() > 0 && te.getChildren().get(0).getContextElements().size() > 0 && te.getParent() != null) {
			TreeElement child = te.getChildren().get(0);
			String test = child.getParentContext();
			setRmAttributeName(test);

			if (interval != null && !StringUtils.isEmpty(test)) {
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
						getManager()
							.addTest(
									new Test(
												getTreeElement(),
												ETestType.Occurrences,
												context,
												ERoleLevel.Error,
												testPrefix + TestGenerator.createCardinalityIntervalCheckLowerBoundary(this, interval,
														getRmAttributeName()) + testPostfix,
												lang("CACardinalityLowerBoundary", quote(getRmAttributeName()),
														wrapValue(interval.getLower())),
												"a",
												getRmAttributeName()));
					}
				}
				if (interval.getUpper() != null) {
					getManager()
						.addTest(
								new Test(
											getTreeElement(),
											ETestType.Occurrences,
											context,
											ERoleLevel.Error,
											testPrefix + TestGenerator.createCardinalityIntervalCheckUpperBoundary(this, interval,
													getRmAttributeName()) + testPostfix,
											lang("CACardinalityUpperBoundary", quote(getRmAttributeName()),
													wrapValue(interval.getUpper())),
											"a",
											getRmAttributeName()));
				}
			}
		}
	}

}

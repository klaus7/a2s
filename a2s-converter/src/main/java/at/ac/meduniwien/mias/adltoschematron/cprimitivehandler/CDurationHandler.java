package at.ac.meduniwien.mias.adltoschematron.cprimitivehandler;

import static at.ac.meduniwien.mias.adltoschematron.helpers.Utils.*;

import org.apache.commons.lang.StringEscapeUtils;
import org.openehr.am.archetype.constraintmodel.primitive.CDuration;
import org.openehr.am.archetype.constraintmodel.primitive.CPrimitive;
import org.openehr.rm.datatypes.quantity.datetime.DvDuration;
import org.openehr.rm.support.basic.Interval;

import at.ac.meduniwien.mias.adltoschematron.TreeElement;
import at.ac.meduniwien.mias.adltoschematron.helpers.SetProperties;
import at.ac.meduniwien.mias.adltoschematron.helpers.Utils;

public class CDurationHandler extends AbstractCPrimitiveHandler implements ICPrimitiveHandler {

	public CDurationHandler(final TreeElement te) {
		super(te);
	}

	@Override
	public void generateTests() {
		super.generateTests();

		CPrimitive cp = getTreeElement().getCpo().getItem();
		CDuration cd = (CDuration) cp;
		DvDuration assignedValue = cd.assignedValue();

		if (assignedValue != null) {
			String ms = assignedValue.getMagnitudeStatus();
			String rmAttributeName = Utils.attributeWrapper(getTreeElement().getCa().getRmAttributeName());

			String op = null;
			if (ms == null || ms.equals("") || ms.equals("=")) {
				op = "=";
			} else if (ms.equals("~")) {
				// don't add test
				op = null;
			} else {
				op = StringEscapeUtils.escapeXml(ms);
			}
			//			} else if (ms.equals("<")) {
			//			} else if (ms.equals(">")) {
			//			} else if (ms.equals("<=")) {
			//			} else if (ms.equals(">=")) {
			//			} else if (ms.equals("~")) {
			//			}

			if (op != null) {
				String test =
						(wrapAttributeFunctionLeft("@" + rmAttributeName) + op + wrapAttributeFunctionRight(wrapValue(cp.assignedValue())));
				addPrimitiveConstraintTest(test, lang("TheValueOfMustBe", rmAttributeName, wrapValue(cp.assignedValue())), rmAttributeName);
			}
		}
		Interval<?> interval = cd.getInterval();

		// only add interval checks if not in raw mode
		if (interval != null && !SetProperties.timeDurationFormat.equalsIgnoreCase("raw")) {
			if (interval.getLower() != null) {
				addPrimitiveConstraintTest(TestGenerator.createIntervalCheckLowerBoundary(this, interval, getRmAttributeName()),
						lang("CIntegerLowerBoundary", quote(getRmAttributeName()), wrapValue(interval.getLower())), getRmAttributeName());
			}
			if (interval.getUpper() != null) {
				addPrimitiveConstraintTest(TestGenerator.createIntervalCheckUpperBoundary(this, interval, getRmAttributeName()),
						lang("CIntegerUpperBoundary", quote(getRmAttributeName()), wrapValue(interval.getUpper())), getRmAttributeName());
			}
		}
	}

	@Override
	public String wrapAttributeFunctionLeft(final String attr) {
		if ("func".equalsIgnoreCase(SetProperties.timeDurationFormat)) {
			return "xs:duration(" + (attr) + ")";
		}
		return super.wrapAttributeFunctionLeft(attr);
	}

	@Override
	public String wrapValue(final Object value) {
		if ("raw".equalsIgnoreCase(SetProperties.timeDurationFormat)) {
			return Utils.quote(((DvDuration) value).toString());
		} else if ("func".equalsIgnoreCase(SetProperties.timeDurationFormat)) {
			String strvalue = ((DvDuration) value).toString();
			strvalue = strvalue.replace(",", ".");
			return "xs:duration(" + Utils.quote(strvalue) + ")";
		} else if ("sec".equalsIgnoreCase(SetProperties.timeDurationFormat)) {
			return ((DvDuration) value).getMagnitude().toString();
		}

		return ((DvDuration) value).toString();
	}

}

package at.ac.meduniwien.mias.adltoschematron.cprimitivehandler;

import static at.ac.meduniwien.mias.adltoschematron.helpers.Utils.*;

import java.util.List;

import org.openehr.am.archetype.constraintmodel.primitive.CInteger;
import org.openehr.am.archetype.constraintmodel.primitive.CPrimitive;
import org.openehr.am.archetype.constraintmodel.primitive.CReal;
import org.openehr.rm.support.basic.Interval;

import at.ac.meduniwien.mias.adltoschematron.TreeElement;
import at.ac.meduniwien.mias.adltoschematron.helpers.Msg;

public class CNumberHandler extends AbstractCPrimitiveHandler {

	public CNumberHandler(final TreeElement te) {
		super(te);
	}

	@Override
	public void generateTests() {
		super.generateTests();

		CPrimitive cp = getTreeElement().getCpo().getItem();

		Interval<?> interval = null;
		List<?> valueList = null;

		if (cp instanceof CInteger) {
			CInteger ci = (CInteger) cp;
			interval = ci.getInterval();
			valueList = ci.getList();
		}

		if (cp instanceof CReal) {
			CReal ci = (CReal) cp;
			interval = ci.getInterval();
			valueList = ci.getList();
		}

		// create interval checks

		if (interval != null) {
			if (interval.getLower() != null) {
				addPrimitiveConstraintTest(TestGenerator.createIntervalCheckLowerBoundary(this, interval, getRmAttributeName()),
						lang("CIntegerLowerBoundary", quote(getRmAttributeName()), wrapValue(interval.getLower())), getRmAttributeName());
			}
			if (interval.getUpper() != null) {
				addPrimitiveConstraintTest(TestGenerator.createIntervalCheckUpperBoundary(this, interval, getRmAttributeName()),
						lang("CIntegerUpperBoundary", quote(getRmAttributeName()), wrapValue(interval.getUpper())), getRmAttributeName());
			}
		}

		// create list checks

		if (valueList != null) {
			String test = TestGenerator.createListCheck(this, getRmAttributeName(), valueList);
			addPrimitiveConstraintTest(test, lang("CIntegerList", "@" + getRmAttributeName(), Msg.get()), getRmAttributeName());
		}
	}

	@Override
	public boolean addAssignedValueTest() {

		if (getCP() instanceof CInteger) {

			CInteger ci = (CInteger) getCP();
			return ci.getList() == null;

		}

		if (getCP() instanceof CReal) {

			CReal ci = (CReal) getCP();
			return ci.getList() == null;

		}

		return super.addAssignedValueTest();
	}

	@Override
	public String wrapValue(final Object value) {
		return value.toString();
	}

}

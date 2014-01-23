package at.ac.meduniwien.mias.adltoschematron.cprimitivehandler;

import static at.ac.meduniwien.mias.adltoschematron.helpers.Utils.*;

import org.apache.commons.lang.StringUtils;
import org.openehr.am.archetype.constraintmodel.CAttribute;
import org.openehr.am.archetype.constraintmodel.primitive.CPrimitive;
import org.openehr.am.archetype.constraintmodel.primitive.CString;

import at.ac.meduniwien.mias.adltoschematron.TreeElement;
import at.ac.meduniwien.mias.adltoschematron.enums.ERoleLevel;
import at.ac.meduniwien.mias.adltoschematron.helpers.Msg;
import at.ac.meduniwien.mias.adltoschematron.helpers.SetProperties;
import at.ac.meduniwien.mias.adltoschematron.helpers.Utils;

public class CStringHandler extends AbstractCPrimitiveHandler {

	private static final String PATTERN_ANY_STRING = ".*";

	public CStringHandler(final TreeElement te) {
		super(te);
	}

	@Override
	public void generateTests() {
		super.generateTests();

		CPrimitive cp = getTreeElement().getCpo().getItem();
		CAttribute ca = getTreeElement().getCa();
		CString cs = (CString) cp;

		ERoleLevel role = ERoleLevel.Error;

		if (!cp.hasAssignedValue() && role.equals(ERoleLevel.Error)) {

			if (!StringUtils.isEmpty(cs.getPattern())) {

				// match string regular expression pattern

				if (!cs.getPattern().equals(PATTERN_ANY_STRING)) {
					Msg.add(lang("ValueOfDoesntMatchPattern", "@" + getRmAttributeName(), Utils.quote(cs.getPattern())));
					addPrimitiveConstraintTest("fn:matches(@" + getRmAttributeName() + ", " + Utils.quote(cs.getPattern()) + ")",
							getRmAttributeName());
				}

			} else if (cs.getList() != null) {

				// match list of possible strings
				//  only accesses this branch, if no pattern was specified!

				String test = TestGenerator.createListCheck(this, getRmAttributeName(), cs.getList());
				addPrimitiveConstraintTest(test, lang("ValueOfNotInStringList", "@" + getRmAttributeName(), Msg.get()),
						getRmAttributeName());

			}
		}
	}

	@Override
	public String wrapAttributeFunction(final String attr) {
		// case insensitive settings true
		if (SetProperties.stringComparisonCaseSensitive) {
			return super.wrapAttributeFunction(attr);
		} else {
			return "lower-case(" + super.wrapAttributeFunction(attr) + ")";
		}
	}

	@Override
	public String getValue() {

		CString cs = (CString) getCP();

		return Utils.returnFirstNotEmpty(super.getValue(), cs.getPattern());

	}

	@Override
	public String wrapValue(final Object value) {
		return Utils.quote(value);
	}

}

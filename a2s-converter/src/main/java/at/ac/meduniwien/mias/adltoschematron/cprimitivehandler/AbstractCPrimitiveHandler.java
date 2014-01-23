package at.ac.meduniwien.mias.adltoschematron.cprimitivehandler;

import static at.ac.meduniwien.mias.adltoschematron.helpers.Utils.*;
import lombok.Getter;
import lombok.Setter;

import org.openehr.am.archetype.constraintmodel.CAttribute;
import org.openehr.am.archetype.constraintmodel.primitive.CPrimitive;

import at.ac.meduniwien.mias.adltoschematron.AdlToSchematronConverter;
import at.ac.meduniwien.mias.adltoschematron.TreeElement;
import at.ac.meduniwien.mias.adltoschematron.data.Test;
import at.ac.meduniwien.mias.adltoschematron.data.Test.ETestType;
import at.ac.meduniwien.mias.adltoschematron.enums.ERoleLevel;
import at.ac.meduniwien.mias.adltoschematron.helpers.Info;
import at.ac.meduniwien.mias.adltoschematron.helpers.Msg;
import at.ac.meduniwien.mias.adltoschematron.helpers.SetProperties;
import at.ac.meduniwien.mias.adltoschematron.helpers.TestManager;
import at.ac.meduniwien.mias.adltoschematron.helpers.Utils;

/**
 * Abstract implemenation of the primitive handler that creates basic checks that apply to almost every data type.
 * 
 * @author Klaus Pfeiffer
 */
public abstract class AbstractCPrimitiveHandler implements ICPrimitiveHandler {

	@Getter
	private final TreeElement treeElement;

	@Getter
	@Setter
	private String rmAttributeName;

	@Getter
	private final TestManager manager;

	public AbstractCPrimitiveHandler(final TreeElement te) {
		this.treeElement = te;
		this.manager = AdlToSchematronConverter.getManager();
		Info.constraints++;
	}

	@Override
	public String getValue() {

		CPrimitive cp = treeElement.getCpo().getItem();
		String value = (String) cp.assignedValue();

		return value;
	}

	@Override
	public void generateTests() {
		CPrimitive cp = getCP();
		CAttribute ca = treeElement.getCa();

		rmAttributeName = Utils.attributeWrapper(ca.getRmAttributeName());

		int severity = SetProperties.testSeverity;

		// add standard tests
		if (addAssignedValueTest() && cp.hasAssignedValue() && ERoleLevel.Error.include(severity)) {

			String test =
					(wrapAttributeFunctionLeft("@" + rmAttributeName) + "=" + wrapAttributeFunctionRight(wrapValue(cp.assignedValue())));
			addPrimitiveConstraintTest(test, lang("TheValueOfMustBe", rmAttributeName, wrapValue(cp.assignedValue())),
					rmAttributeName);

		}

		if (cp.hasAssumedValue() && ERoleLevel.Info.include(severity)) {

			String test =
					(wrapAttributeFunctionLeft("@" + rmAttributeName) + "=" + wrapAttributeFunctionRight(wrapValue(cp.assumedValue())));
			addPrimitiveConstraintTest(ERoleLevel.Info,
					test,
					lang("TheValueOfHasAssumedValue", rmAttributeName, wrapValue(cp.assumedValue())),
					rmAttributeName);

		}

		if (cp.hasDefaultValue() && ERoleLevel.Info.include(severity)) {

			String test =
					(wrapAttributeFunctionLeft("@" + rmAttributeName) + "=" + wrapAttributeFunctionRight(wrapValue(cp.defaultValue())));
			addPrimitiveConstraintTest(ERoleLevel.Info,
					test,
					lang("TheValueOfHasDefaultValue", rmAttributeName, wrapValue(cp.defaultValue())),
					rmAttributeName);

		}
	}

	/**
	 * Override if subtype may not add test for assigned value.
	 * 
	 * @return true
	 */
	public boolean addAssignedValueTest() {
		return true;
	}

	/**
	 * @return {@link CPrimitive} item object of tree element.
	 */
	public CPrimitive getCP() {
		return treeElement.getCpo().getItem();
	}

	@Override
	public String wrapAttributeFunction(final String attr) {
		return attr;
	}

	@Override
	public String wrapAttributeFunctionLeft(final String attr) {
		return wrapAttributeFunction(attr);
	}

	@Override
	public String wrapAttributeFunctionRight(final String attr) {
		return wrapAttributeFunction(attr);
	}

	/**
	 * Convenience method for adding a test.
	 * 
	 * @param type
	 * @param context
	 * @param test
	 * @param msg
	 */
	public Test addTest(final ETestType type, final String context, final String test, final String msg,
			final String mainElement) {
		return manager.addTest(new Test(treeElement, type, context, ERoleLevel.Error, test, msg, "", mainElement));
	}

	public Test addTest(final ERoleLevel role, final ETestType type, final String context, final String test,
			final String msg, final String mainElement) {
		return manager.addTest(new Test(treeElement, type, context, role, test, msg, "", mainElement));
	}

	public Test addPrimitiveConstraintTest(final String test, final String msg, final String mainElement) {
		return manager.addTest(new Test(treeElement, ETestType.PrimitiveConstraints, treeElement.getContext(),
										ERoleLevel.Error, test, msg, "", mainElement));
	}

	public Test addPrimitiveConstraintTest(final String test, final String msg, final String flags,
			final String mainElement) {
		return manager.addTest(new Test(treeElement, ETestType.PrimitiveConstraints, treeElement.getContext(),
										ERoleLevel.Error, test, msg, flags, mainElement));
	}

	public Test addPrimitiveConstraintTest(final ERoleLevel role, final String test, final String msg,
			final String mainElement) {
		return addTest(role, ETestType.PrimitiveConstraints, treeElement.getContext(), test, msg, mainElement);
	}

	public Test addPrimitiveConstraintTest(final String test, final String mainElement) {
		return addTest(ETestType.PrimitiveConstraints, treeElement.getContext(), test, Msg.get(), mainElement);
	}

}

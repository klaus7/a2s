package at.ac.meduniwien.mias.adltoschematron.cprimitivehandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.extern.log4j.Log4j;

import org.openehr.am.archetype.constraintmodel.CAttribute;
import org.openehr.am.archetype.constraintmodel.CComplexObject;
import org.openehr.am.archetype.constraintmodel.CPrimitiveObject;
import org.openehr.am.archetype.constraintmodel.primitive.CBoolean;
import org.openehr.am.archetype.constraintmodel.primitive.CDate;
import org.openehr.am.archetype.constraintmodel.primitive.CDateTime;
import org.openehr.am.archetype.constraintmodel.primitive.CDuration;
import org.openehr.am.archetype.constraintmodel.primitive.CInteger;
import org.openehr.am.archetype.constraintmodel.primitive.CPrimitive;
import org.openehr.am.archetype.constraintmodel.primitive.CReal;
import org.openehr.am.archetype.constraintmodel.primitive.CString;
import org.openehr.am.archetype.constraintmodel.primitive.CTime;
import org.openehr.rm.support.basic.Interval;

import at.ac.meduniwien.mias.adltoschematron.TreeElement;
import at.ac.meduniwien.mias.adltoschematron.data.Attribute;
import at.ac.meduniwien.mias.adltoschematron.data.TestElement;
import at.ac.meduniwien.mias.adltoschematron.helpers.CdaSpecific;
import at.ac.meduniwien.mias.adltoschematron.helpers.Msg;
import at.ac.meduniwien.mias.adltoschematron.helpers.Utils;

/**
 * Generates the tests and provides methods for this task.
 * 
 * @author Klaus Pfeiffer
 */
@Log4j
public final class TestGenerator {

	private static final boolean FROM_CA = true;
	private static final boolean FROM_CCO = false;

	@CdaSpecific
	public static void generate(final TreeElement te) {

		if (te.hasCpo() && !te.getParent().getName(FROM_CCO).endsWith("II")) {
			// do not process II (Instance Identifier) 
			// we do not want to assert those. We already did in the required statements.
			cpoGenerate(te);
		}

		if (te.hasCa()) {
			caGenerate(te);
		}

		if (te.hasCco()) {
			ccoGenerate(te);
		}

	}

	/**
	 * Generate the tests for the {@link CPrimitiveObject}.
	 * 
	 * @param te tree element to generate tests for
	 */
	private static void cpoGenerate(final TreeElement te) {
		String rmAttributeName = Utils.attributeWrapper(te.getCa().getRmAttributeName());

		// check if attribute name was already used in context elements
		if (te.isAttributeInContextElements(te.getContextElements(), rmAttributeName)) {

			// then we do not generate tests on it - would be pointless
			return;

		}

		ICPrimitiveHandler handler = getCPrimitiveHandlerForTreeElement(te);
		if (handler != null) {
			handler.generateTests();
		}

	}

	/**
	 * Generate the tests for the {@link CAttribute}.
	 * 
	 * @param te tree element to generate tests for
	 */
	private static void caGenerate(final TreeElement te) {

		new CAttributeHandler(te).generateTests();

	}

	/**
	 * Generate the tests for the {@link CComplexObject}.
	 * 
	 * @param te tree element to generate tests for
	 */
	private static void ccoGenerate(final TreeElement te) {

		new CComplexObjectHandler(te).generateTests();
		
	}

	public static ICPrimitiveHandler getCPrimitiveHandlerForTreeElement(final TreeElement te) {
		CPrimitive cp = te.getCpo().getItem();
		ICPrimitiveHandler handler = null;

		if (cp instanceof CBoolean) {
			handler = new CBooleanHandler(te);
		}
		if (cp instanceof CInteger || cp instanceof CReal) {
			handler = new CNumberHandler(te);
		}
		if (cp instanceof CString) {
			handler = new CStringHandler(te);
		}
		if (cp instanceof CDateTime
			|| cp instanceof CDate
			|| cp instanceof CTime) {
			handler = new CDateTimeHandler(te);
		}
		if (cp instanceof CDuration) {
			handler = new CDurationHandler(te);
		}
		if (handler == null) {
			log.warn("Handler is null! CPrimitive is of type " + cp.getType());
		}

		return handler;
	}

	/**
	 * Create a test where an attribute gets checked if it contains on of multiple possible values.
	 * 
	 * @param rmAttributeName
	 * @param entries valid values
	 * @return constructed test string
	 */
	public static String createListCheck(final ICPrimitiveHandler handler, final String rmAttributeName, final List<?> entries) {
		TestElement e = new TestElement();
		e.setOperator("or");

		List<Attribute> attributes = new ArrayList<Attribute>();
		String msgStrings = "";

		for (Iterator<?> iterator = entries.iterator(); iterator.hasNext();) {
			Object s = iterator.next();
			attributes.add(new Attribute(handler.wrapAttributeFunctionLeft("@" + rmAttributeName), "=" + handler
				.wrapAttributeFunctionRight(handler.wrapValue(s))));
			msgStrings += handler.wrapValue(s);

			if (iterator.hasNext()) {
				msgStrings += ", ";
			}

		}

		e.setAttributes(attributes);
		Msg.add("[" + msgStrings + "]");

		return e.write();
	}

	/**
	 * @param interval Interval<?>
	 * @param attributename String
	 * @return test that checks boundary of attributename
	 */
	public static String createIntervalCheckLowerBoundary(final ICPrimitiveHandler handler, final Interval<?> interval,
			final String attributename) {

		String test = "";
		if (interval.getLower() != null) {
			test += handler.wrapAttributeFunctionLeft("@" + attributename);
			if (interval.isLowerIncluded()) {
				test += "&gt;=";
			} else {
				test += "&gt;";
			}
			test += handler.wrapAttributeFunctionRight(handler.wrapValue(interval.getLower()));
		}

		return test;
	}

	/**
	 * @param interval Interval<?>
	 * @param attributename String
	 * @return test that checks boundary of attributename
	 */
	public static String createIntervalCheckUpperBoundary(final ICPrimitiveHandler handler, final Interval<?> interval,
			final String attributename) {

		String test = "";
		if (interval.getUpper() != null) {
			test += handler.wrapAttributeFunctionLeft("@" + attributename);
			if (interval.isUpperIncluded()) {
				test += "&lt;=";
			} else {
				test += "&lt;";
			}
			test += handler.wrapAttributeFunctionRight(handler.wrapValue(interval.getUpper()));
		}

		return test;
	}

	/**
	 * @param interval Interval<?>
	 * @param attributename String
	 * @return test that checks count of attributename
	 */
	public static String createCardinalityIntervalCheckLowerBoundary(final ICPrimitiveHandler handler, final Interval<?> interval,
			final String attributename) {

		String test = "";
		if (interval.getLower() != null) {
			test += "count(" + (attributename) + ")";
			if (interval.isLowerIncluded()) {
				test += "&gt;=";
			} else {
				test += "&gt;";
			}
			test += handler.wrapValue(interval.getLower());
		}

		return test;
	}

	/**
	 * @param interval Interval<?>
	 * @param attributename String
	 * @return test that checks count of attributename
	 */
	public static String createCardinalityIntervalCheckUpperBoundary(final ICPrimitiveHandler handler, final Interval<?> interval,
			final String attributename) {

		String test = "";
		if (interval.getUpper() != null) {
			test += "count(" + (attributename) + ")";
			if (interval.isUpperIncluded()) {
				test += "&lt;=";
			} else {
				test += "&lt;";
			}
			test += handler.wrapValue(interval.getUpper());
		}

		return test;
	}

}

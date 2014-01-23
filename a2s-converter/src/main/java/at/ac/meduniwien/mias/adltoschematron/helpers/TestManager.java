package at.ac.meduniwien.mias.adltoschematron.helpers;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import lombok.Getter;

import org.apache.commons.lang.StringUtils;
import org.openehr.am.archetype.constraintmodel.CAttribute.Existence;

import at.ac.meduniwien.mias.adltoschematron.context.ContextElement;
import at.ac.meduniwien.mias.adltoschematron.context.ContextElementAttribute;
import at.ac.meduniwien.mias.adltoschematron.data.Assertion;
import at.ac.meduniwien.mias.adltoschematron.data.Pattern;
import at.ac.meduniwien.mias.adltoschematron.data.Rule;
import at.ac.meduniwien.mias.adltoschematron.data.Test;
import at.ac.meduniwien.mias.adltoschematron.data.Test.ETestType;
import at.ac.meduniwien.mias.adltoschematron.enums.ERoleLevel;

/**
 * Used to generate the patterns and rules and does some post processing to the generated tests.
 * 
 * @author Klaus
 */
public class TestManager {

	/**
	 * Holds every generated test.
	 */
	@Getter
	private final List<Test> tests = new ArrayList<Test>();

	/**
	 * @param t test to add
	 * @return the added test
	 */
	public Test addTest(final Test t) {
		if (!StringUtils.isEmpty(t.getTest()) && !tests.contains(t)) {
			tests.add(t);
		} else {
			// test empty or already generated
			Info.skipped++;
		}
		return t;
	}

	/**
	 * Generate patterns, rules, asserts and post-process tests.
	 * 
	 * @return patterns
	 */
	public List<Pattern> generateRulesAndPatterns() {

		List<Pattern> patterns = new ArrayList<Pattern>();

		for (Test t : new ArrayList<Test>(tests)) {

			// post-process tests
			//
			// generate tests for attributes and values in elements likewise
			// also, if element/attribute is optional join tests with OR
			// if element/attribute is required then join with XOR

			if (t.getTreeElement().hasCpo() && t.getTreeElement().getCa().getExistence().equals(Existence.OPTIONAL)) {

				if (t.getTest().contains("@") && !t.getFlags().contains("a")) {
					addXorConditionTest(t);

					t.setTest("(not(@" + Utils.attributeWrapper(t.getTreeElement().getCa().getRmAttributeName()) + ")"
								+ " and not(hl7:" + Utils.attributeWrapper(t.getTreeElement().getCa().getRmAttributeName()) + "))"
								+ " or ((" + t.getTest() + ") "
								+ " or (" + Utils.removeAttributeChar(t.getTest()) + "))");

				} else {

					addXorConditionTest(t);
					t.setTest("not(@" + Utils.attributeWrapper(t.getTreeElement().getCa().getRmAttributeName()) + ") or (" + t.getTest() + ")");

				}

			} else {

				if (t.getTest().contains("@") && !t.getFlags().contains("a") && !StringUtils.isEmpty(t.getMainElement())) {

					addXorConditionTest(t);

					t.setTest("((" + t.getTest() + ") or (" + Utils.removeAttributeChar(t.getTest()) + "))");
				}
			}
		}

		for (Test t : tests) {
			createRule(t.getTreeElement().getInheritedContextElements(), t.getType(), t.getContext(), t.getRole(), t.getTest(), t.getMsg(),
					patterns);
		}

		return patterns;
	}

	private void addXorConditionTest(final Test t) {
		// XPath does not provide xor condition out of the box, so we have to emulate it with "x and not(y)  or  y and not(x)"
		String element = "@" + t.getMainElement();
		//					String test = "(" + element + " and not(" + Utils.removeAttributeChar(element) + ")) or ("
		//							+ Utils.removeAttributeChar(element) + " and not(" + element + "))";

		String test = "not(" + element + " and " + Utils.removeAttributeChar(element) + ")";
		addTest(new Test(t.getTreeElement(), t.getType(), t.getContext(), t.getRole(), test,
							Utils.lang("XMLAttributeElementXORCondition", t.getMainElement()), "", ""));

	}

	/**
	 * Search for pattern with specified name.
	 * 
	 * @param name pattern name
	 * @param patterns list of patterns
	 * @return pattern, new if not found
	 */
	public static Pattern getPatternFromName(final String name, final List<Pattern> patterns) {

		for (Pattern p : patterns) {
			if (p.getName().equals(name)) {
				return p;
			}
		}

		// no pattern found -> create new one
		Pattern p = new Pattern(name);
		patterns.add(p);

		return p;
	}

	/**
	 * Creates the name for the assignment to the patterns.
	 * 
	 * @param templateId
	 * @return name from templateId.
	 */
	public static String getNameFromContextElement(final Deque<ContextElement> aContextElementStack) {

		if (aContextElementStack.isEmpty()) {
			return "root"; // unassigned; rules without specific templateId context
		}

		for (ContextElement contextElement : aContextElementStack) {

			if (contextElement.getElement().equalsIgnoreCase("templateId")) {

				for (ContextElementAttribute attr : contextElement.getAttributes()) {
					if (attr.getName().equalsIgnoreCase("root")) {
						return "template-" + attr.getValue();
					}
				}

			} else {

				return "element-" + contextElement.getElement();

			}

		}
		return "root";
	}

	/**
	 * Create a new rule with context.
	 * 
	 * @param templateId used to retrieve the name for the pattern.
	 * @param type only used for the comment.
	 * @param context context of the rule
	 * @param patterns list of patterns
	 * @return the created rule
	 */
	private static Rule createRule(final Deque<ContextElement> templateId, final ETestType type, final String context,
			final List<Pattern> patterns) {
		Rule r = null;

		for (Rule rule : getPatternFromName(getNameFromContextElement(templateId), patterns).getRules()) {
			if (rule.getContext().equals(context)) {
				r = rule;
			}
		}
		if (r == null) {
			r = new Rule(context, type.toString());
			getPatternFromName(getNameFromContextElement(templateId), patterns).getRules().add(r);
		}
		return r;
	}

	/**
	 * Create a new rule with context.
	 * 
	 * @param templateId
	 * @param type only used for the comment.
	 * @param context
	 * @param level
	 * @param test
	 * @param message
	 * @param patterns
	 * @return the created rule
	 */
	public static Rule createRule(final Deque<ContextElement> templateId, final ETestType type, final String context,
			final ERoleLevel level,
			final String test, final String message, final List<Pattern> patterns) {
		Rule r = createRule(templateId, type, context, patterns);

		//		if (SetProperties.ignoreDuplicateTests) {
		//    		// check if test already exists
		//    		for (Assert a : r.assertions) {
		//    			if (a.getTest().equals(test) && a.getLevel().equals(level)) {
		//    				return r;
		//    			}
		//    		}
		//		}

		Assertion a = new Assertion(test, level, message);
		r.getAssertions().add(a);
		return r;
	}

}

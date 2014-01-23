package at.ac.meduniwien.mias.adltoschematron;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import lombok.extern.log4j.Log4j;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

@Log4j
public class AdlToSchematronConverterTest {
	
	private String lastReport;

	@Test
	public void testBasicFailure() throws ParseException {
		String adlFile = "adl/test/adl-test-entry.basic_types.test.adl";
		String xmlFile = "xml/test/test-entry-basic-failure.xml";
		assertEquals("Generation of basic constraint checks or testing error.", 146 + 14, AdlToSchematronConverter.run(adlFile, xmlFile));

		readReport();
		reportContains("Element 'existence_check' ist verpflichtend.");
		reportContains("Das Element 'cardinality_check1' muss mindestens 2-mal vorkommen.");
		reportContains("Das Element 'cardinality_check2' darf maximal 3-mal vorkommen.");
		reportContains("string_attr1 darf entweder nur als XML-Element oder nur als XML-Attribut vorkommen.");
	}
	
	@Test
	public void testBasicSuccess() throws ParseException {
		String adlFile = "adl/test/adl-test-entry.basic_types.test.adl";
		String xmlFile = "xml/test/test-entry-basic-success.xml";
		assertEquals("Generation of basic constraint checks or testing error.", 0, AdlToSchematronConverter.run(adlFile, xmlFile));
	}
	
	@Test
	public void testNestedFailure() throws ParseException {
		String adlFile = "adl/test/participant.test.adl";
		String xmlFile = "xml/test/test-entry-nested-failure.xml";
		assertEquals("Generation of nested context constraint checks or testing error.", 6, AdlToSchematronConverter.run(adlFile, xmlFile));
	}
	
	@Test
	public void testNestedSuccess() throws ParseException {
		String adlFile = "adl/test/participant.test.adl";
		String xmlFile = "xml/test/test-entry-nested-success.xml";
		assertEquals("Generation of nested context constraint checks or testing error.", 0, AdlToSchematronConverter.run(adlFile, xmlFile));
	}
	
	/**
	 * This test checks if the required statements of the context elements are built.
	 * @throws ParseException thrown when parsing runs into error
	 */
	@Test
	public void testEmpty() throws ParseException {
		String adlFile = "adl/test/participant.test.adl";
		String xmlFile = "xml/test/test-entry-empty.xml";
		assertEquals("Generation of checks or testing error.", 1, AdlToSchematronConverter.run(adlFile, xmlFile));
	}
	
	@Test
	public void testOccurrences() throws ParseException {
		String adlFile = "adl/test/occurrences.test.adl";
		String xmlFile = "xml/test/test-entry-occurrences-success.xml";
		assertEquals("Generation of checks or testing error.", 0, AdlToSchematronConverter.run(adlFile, xmlFile));

		xmlFile = "xml/test/test-entry-occurrences-failure.xml";
		assertEquals("Generation of checks or testing error.", 3, AdlToSchematronConverter.run(adlFile, xmlFile));

		readReport();
		reportContains("Der Wert von string_attr MUSS 'anything' sein.");
		reportContains("Das Element '//*[hl7:templateId[@assigningAuthorityName='ELGA' and @root='1.3']]' darf maximal 3-mal vorkommen.");
		reportContains("Das Element '//*[hl7:templateId[@assigningAuthorityName='ELGA' and @root='1.2']]' muss mindestens 1-mal vorkommen.");
	}

	@Test
	public void testCardinality() throws ParseException {
		String adlFile = "adl/test/cardinality.test.adl";
		String xmlFile = "xml/test/test-entry-cardinality-success.xml";
		assertEquals("Generation of checks or testing error.", 0, AdlToSchematronConverter.run(adlFile, xmlFile));

		xmlFile = "xml/test/test-entry-cardinality-failure.xml";
		assertEquals("Generation of checks or testing error.", 2, AdlToSchematronConverter.run(adlFile, xmlFile));

		readReport();
		reportContains("Element 'ca2' ist verpflichtend.");
		reportContains("Das Element 'ca1' darf maximal 2-mal vorkommen.");
	}

	/**
	 * Read report to a string.
	 */
	private void readReport() {
		try {
			FileReader fr = new FileReader(new File(AdlToSchematronConverter.reportOutputFile));
			BufferedReader br = new BufferedReader(fr);

			String line;
			String report = "";
			while ((line = br.readLine()) != null) {
				report += line;
			}
			lastReport = report;
			br.close();
			fr.close();
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
		}

	}

	private void reportContains(final String s) {
		assertTrue(lastReport.contains(s));
	}

}

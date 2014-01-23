package at.ac.meduniwien.mias.adltoschematron;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import se.acode.openehr.parser.ADLParser;
import se.acode.openehr.parser.ADLParserTokenManager;

/**
 * With the wrapper it is possible to fix the archetype before processing it.
 * 
 * @author Klaus Pfeiffer
 */
public class AdlParserWrapper extends ADLParser {

	/**
	 * @param tm
	 */
	public AdlParserWrapper(final ADLParserTokenManager tm) {
		super(tm);
	}

	/**
	 * @param file
	 * @param missingLanguageCompatible
	 * @param emptyPurposeCompatible
	 * @throws IOException
	 */
	public AdlParserWrapper(final File file, final boolean missingLanguageCompatible, final boolean emptyPurposeCompatible)
																															throws IOException {
		super(file, missingLanguageCompatible, emptyPurposeCompatible);

		//		parser = new ADLParser(AdlParserRegexFixer.fixAdlFile(adlFile), true, true);

	}

	/**
	 * @param file
	 * @throws IOException
	 */
	public AdlParserWrapper(final File file) throws IOException {
		super(file);
	}

	/**
	 * @param input
	 * @param missingLanguageCompatible
	 * @param emptyPurposeCompatible
	 */
	public AdlParserWrapper(final InputStream input, final boolean missingLanguageCompatible, final boolean emptyPurposeCompatible) {
		super(input, missingLanguageCompatible, emptyPurposeCompatible);
	}

	/**
	 * @param stream
	 * @param encoding
	 */
	public AdlParserWrapper(final InputStream stream, final String encoding) {
		super(stream, encoding);
	}

	/**
	 * @param stream
	 */
	public AdlParserWrapper(final InputStream stream) {
		super(stream);
	}

	/**
	 * @param reader
	 * @param missingLanguageCompatible
	 * @param emptyPurposeCompatible
	 */
	public AdlParserWrapper(final Reader reader, final boolean missingLanguageCompatible, final boolean emptyPurposeCompatible) {
		super(reader, missingLanguageCompatible, emptyPurposeCompatible);
	}

	/**
	 * @param stream
	 */
	public AdlParserWrapper(final Reader stream) {
		super(stream);
	}

	/**
	 * @param value
	 * @param missingLanguageCompatible
	 * @param emptyPurposeCompatible
	 */
	public AdlParserWrapper(final String value, final boolean missingLanguageCompatible, final boolean emptyPurposeCompatible) {
		super(value, missingLanguageCompatible, emptyPurposeCompatible);
	}

	/**
	 * @param value
	 */
	public AdlParserWrapper(final String value) {
		super(value);
	}

}

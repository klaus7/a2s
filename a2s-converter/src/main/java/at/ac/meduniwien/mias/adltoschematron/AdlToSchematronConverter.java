package at.ac.meduniwien.mias.adltoschematron;

import static at.ac.meduniwien.mias.adltoschematron.helpers.Utils.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import lombok.Getter;
import lombok.extern.log4j.Log4j;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.StringUtils;
import org.openehr.am.archetype.Archetype;
import org.openehr.am.archetype.assertion.Assertion;
import org.openehr.am.archetype.assertion.ExpressionBinaryOperator;
import org.openehr.am.archetype.assertion.ExpressionLeaf;
import org.openehr.am.archetype.assertion.OperatorKind;
import org.openehr.am.archetype.constraintmodel.ArchetypeSlot;
import org.openehr.am.archetype.constraintmodel.CAttribute;
import org.openehr.am.archetype.constraintmodel.CComplexObject;
import org.openehr.am.archetype.constraintmodel.CObject;
import org.openehr.am.archetype.constraintmodel.CPrimitiveObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import se.acode.openehr.parser.ADLParser;
import se.acode.openehr.parser.ParseException;
import at.ac.meduniwien.mias.adltoschematron.context.ContextElement;
import at.ac.meduniwien.mias.adltoschematron.cprimitivehandler.TestGenerator;
import at.ac.meduniwien.mias.adltoschematron.data.Pattern;
import at.ac.meduniwien.mias.adltoschematron.helpers.IConstants;
import at.ac.meduniwien.mias.adltoschematron.helpers.Info;
import at.ac.meduniwien.mias.adltoschematron.helpers.PathUtils;
import at.ac.meduniwien.mias.adltoschematron.helpers.SetProperties;
import at.ac.meduniwien.mias.adltoschematron.helpers.TestManager;
import at.ac.meduniwien.mias.adltoschematron.helpers.UserInterfaceProvider;
import at.ac.meduniwien.mias.adltoschematron.helpers.Utils;
import at.ac.meduniwien.mias.adltoschematron.helpers.Writer;

/**
 * Base-Class for the Adl-to-Schematron-Converter-Application.
 * 
 * @author Klaus Pfeiffer
 */
@Log4j
public class AdlToSchematronConverter implements IConstants {

	/** Configurable parser properties. */
	private Properties parserProperties;

	/** List of Schematron patterns. */
	private List<Pattern> patterns;

	/** List of ADL-Files that get searched for in case of an archetype slot. */
	private final List<File> adlFiles;

	/** List of pre-configured context elements. */
	public static List<ContextElement> fixedContextElements;

	/** configuration folder. */
	private static String confFolder = null;

	/** schematron output file. */
	public static String outputFile = "schematron.sch";

	/** report output file. */
	public static String reportOutputFile = "report.xml";

	/** manages all generated tests. */
	@Getter
	private static TestManager manager;

	/**
	 * Returned status code:
	 * <ul>
	 * <li>0 ... all ok</li>
	 * <li>-1 ... ran into error -> look into log for specific description</li>
	 * <li>n ... number of failed assertions in report</li>
	 * </ul>
	 * 
	 * @param args program arguments
	 * @throws org.apache.commons.cli.ParseException
	 */
	public static void main(final String[] args) throws org.apache.commons.cli.ParseException {

		Options options = new Options();

		options.addOption("f", true, "The adl file to process. (required)");
		options.addOption("t", true, "The xml file to test the generated Schematron-File with. (optional)");
		options.addOption("c", true,
				"The configuration folder. If not specified, default configuration is loaded. (optional)");
		options.addOption("o", true, "The schematron output file (default: schematron.sch)");
		options.addOption("r", true, "The report output file (default: report.xml)");

		CommandLineParser parser = new PosixParser();
		CommandLine cmd = parser.parse(options, args);

		String adlFile = cmd.getOptionValue("f");
		String xmlFile = cmd.getOptionValue("t");
		confFolder = cmd.getOptionValue("c");
		outputFile = cmd.getOptionValue("o", "schematron.sch");
		reportOutputFile = cmd.getOptionValue("r", "report.xml");

		if (StringUtils.isEmpty(adlFile)) {

			HelpFormatter formatter = new HelpFormatter();
			System.out.println("version: " + AdlToSchematronConverter.class.getPackage().getImplementationVersion());
			formatter.printHelp("java -jar a2s.jar", options);

		} else {

			int returnCode = run(adlFile, xmlFile);
			System.exit(returnCode);
		}
	}

	/**
	 * Static run method to create schematron schema from adlFile and validate the xmlFile with it.
	 * 
	 * @param adlFile adl file to create schematron from.
	 * @param xmlFile xml file to validate with.
	 * @return number of assertion errors
	 */
	public static int run(final String adlFile, final String xmlFile) {

		try {

			if (!StringUtils.isEmpty(adlFile)) {

				new AdlToSchematronConverter(adlFile);

			}

			if (!StringUtils.isEmpty(xmlFile)) {

				return validateXmlWithSchematronFile(xmlFile);

			} else {

				return 0;

			}

			// Properties
		} catch (FileNotFoundException e) {

			log.error("FileNotFoundException", e);

		} catch (IOException e) {

			log.error("IOException", e);

			// ADLParser
		} catch (ParseException e) {

			log.error("ParseException", e);

		} catch (Exception e) {

			log.error("Exception", e);

		}

		return -1;
	}

	/**
	 * Load properties from classpath or from configuration folder, if it was provided with the program arguments.
	 * 
	 * @param propFileName name of properties file
	 * @return {@link Properties} instance
	 * @throws IOException thrown when file couldn't be found
	 */
	private Properties getPropertiesFromClasspath(final String propFileName) throws IOException {

		if (!StringUtils.isEmpty(confFolder)) {

			Properties prop = Utils.loadProperties(new File(confFolder + File.separator + propFileName));

			if (prop != null) {

				return prop;

			} else {

				log.info("Couldn't load custom properties: " + propFileName);

			}

		}

		Properties props = new Properties();
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(propFileName);

		if (inputStream == null) {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}

		props.load(inputStream);

		return props;
	}

	/**
	 * Default constructor.
	 * 
	 * @param adlFileString
	 * @throws Exception
	 * @throws ParseException
	 */
	public AdlToSchematronConverter(final String adlFileString) throws ParseException, FileNotFoundException,
																IOException, Exception {

		patterns = new ArrayList<Pattern>();
		adlFiles = new ArrayList<File>();

		createSchematron(adlFileString);

	}

	@SuppressWarnings("unchecked")
	private Archetype init(final String adlFileString) throws IOException, FileNotFoundException, ParseException,
														Exception {

		if (!StringUtils.isEmpty(confFolder)) {
			log.info("Use custom configuration folder: " + confFolder);
		}

		Utils.languageProperties = getPropertiesFromClasspath("language.properties");

		parserProperties = getPropertiesFromClasspath("parser.properties");

		SetProperties.ignoreDuplicateTests =
				Boolean.parseBoolean(parserProperties.getProperty("ignoreDuplicateTests", "false"));
		SetProperties.ignorePatterns = parserProperties.getProperty("ignorePatterns", "").split(",");
		SetProperties.adlFolder = parserProperties.getProperty("adlFolder", "");
		SetProperties.maxNumberArchetypeLevels =
				Integer.valueOf(parserProperties.getProperty("maxNumberArchetypeLevels", "3"));
		SetProperties.testSeverity =
				Integer.valueOf(parserProperties.getProperty("testSeverity", "5"));
		SetProperties.dateFormat = (parserProperties.getProperty("dateFormat", "yyyyMMddHHmmssZ"));
		SetProperties.stringComparisonCaseSensitive =
				Boolean.valueOf((parserProperties.getProperty("stringComparisonCaseSensitive", "false")));
		SetProperties.timeDurationFormat = (parserProperties.getProperty("timeDurationFormat", "raw"));
		SetProperties.defaultTimeZone = (parserProperties.getProperty("defaultTimeZone", "00:00"));

		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath:contextElements.xml");
		fixedContextElements = ctx.getBean("contextElements", List.class);

		Writer.resetOutput();

		getAdlFiles(SetProperties.adlFolder);

		File adlFile = new File(adlFileString);

		if (!adlFile.exists()) {
			// exit with error code
			throw new FileNotFoundException("adl file doesn't exist!");
		}

		log.info("Start parsing adl file ...");

		ADLParser parser;

		parser = new AdlParserWrapper(adlFile, true, true);
		Archetype archetype = parser.parse();
		return archetype;
	}

	/**
	 * 
	 * @param adlFileString adl file to create archetype from
	 * @throws ParseException thrown on error while parsing the adl to the aom
	 * @throws FileNotFoundException thrown when adl file could not be found
	 * @throws IOException thrown when adl file could not be read
	 * @throws Exception adl parser also throws general exception
	 */
	private void createSchematron(final String adlFileString) throws ParseException, FileNotFoundException,
																IOException, Exception {

		Archetype archetype = init(adlFileString);

		// constraints are in the definition
		CComplexObject cComplexObject = archetype.getDefinition(); // Definition -> Constraints

		log.info("Start processing archetype object model ...");

		// start at level zero
		PathUtils.level = 0;
		Info.init();

		// create root node
		TreeElement rootNode = new TreeElement(null, null, (CComplexObject) null, PathUtils.level);

		// recursivly start processing objects and building TreeElement tree
		processCComplexObject(null, cComplexObject, rootNode);

		// recursivly process own model of constraints
		processTreeElement(rootNode);

		log.info("Generate tests ...");

		manager = new TestManager();
		processTestGeneration(rootNode);

		patterns = manager.generateRulesAndPatterns();

		List<String> ignorelist = new ArrayList<String>();
		ignorelist = Arrays.asList(SetProperties.ignorePatterns);

		// write everything to output

		Writer.start();
		Writer.writeHeader();

		for (final Pattern p : patterns) {
			if (!ignorelist.contains(p.getName().toLowerCase())) {
				Writer.w(p.write());
			}
		}
		Writer.writeFooter();
		Writer.stop();

		log.info("Schematron generation done!");

		String format = "%5d";

		log.info("number of patterns:   " + String.format(format, Info.patterns));
		log.info("number of rules:      " + String.format(format, Info.rules));
		//		log.info("number of constraints:         " + String.format(format, Info.constraints));
		//		log.info("number of skipped constraints: " + String.format(format, Info.skipped));
		log.info("number of assertions: " + String.format(format, Info.assertions));
	}

	/**
	 * Get ADL files from adlFolder.
	 * 
	 * @param adlFolder adl folder to retrieve archetypes from.
	 */
	public void getAdlFiles(final String adlFolder) {
		try {

			File folder = new File(adlFolder);
			FilenameFilter filter = new SuffixFileFilter(".adl");

			for (File f : folder.listFiles(filter)) {
				// TODO make it recursive to get all .adl from every sub-folder!
				adlFiles.add(f);
			}

		} catch (Exception e) {

			log.warn("Couldn't retrieve adl files for automatic archetype slot processing.");

		}
	}

	/**
	 * Recursivly process all CAttribute- and CComplexObjects.
	 * 
	 * @param ca last {@link CAttribute} object
	 * @param cco {@link CComplexObject} object to process
	 * @param parent parent tree element
	 */
	public void processCComplexObject(final CAttribute ca, final CComplexObject cco, final TreeElement parent) {
		increaseLevel();

		TreeElement tree = new TreeElement(parent, ca, cco, PathUtils.level);

		// process attributes (XML elements)
		for (CAttribute nextCa : cco.getAttributes()) {

			processCAttribute(nextCa, tree);

		}

		decreaseLevel();
	}

	/**
	 * Recursivly process all CAttribute- and CComplexObjects.
	 * 
	 * @param ca {@link CAttribute}
	 * @param parent parent tree element
	 */
	private void processCAttribute(final CAttribute ca, final TreeElement parent) {
		increaseLevel();

		// process children
		for (CObject co : ca.getChildren()) {
			if (co instanceof CComplexObject) {

				processCComplexObject(ca, (CComplexObject) co, parent);

			} else if (co instanceof CPrimitiveObject) {

				processCPrimitiveObject(ca, (CPrimitiveObject) co, parent);

			} else if (co instanceof ArchetypeSlot) {

				ArchetypeSlot as = (ArchetypeSlot) co;

				Set<Assertion> includes = as.getIncludes();
				Set<Assertion> excludes = as.getExcludes();

				final List<File> files = new ArrayList<File>();
				final StringBuilder rules = new StringBuilder();

				// include all adl files with the include rules
				// rules.append(IWriter.NEWLINE + IWriter.INDENT1 + "Include-Rules" + IWriter.NEWLINE);
				if (includes != null) {
					for (Assertion ass : includes) {
						ExpressionBinaryOperator ebo = (ExpressionBinaryOperator) ass.getExpression();
						ExpressionLeaf leftOperator = (ExpressionLeaf) ebo.getLeftOperand();
						if (leftOperator.getType().equalsIgnoreCase(ExpressionLeaf.STRING)) {
							String leftItem = (String) leftOperator.getItem();
							if (leftItem.matches("archetype_id/value")
								&& ebo.getOperator().equals(OperatorKind.OP_MATCHES)) {
								String include =
										ebo.getRightOperand().toString()
											.substring(1, ebo.getRightOperand().toString().length() - 1);
								rules.append(include);
								files.addAll(getAdlFilesWithRegex(include));
							}
						}
					}
				}

				// remove ADL files if the exclude-rules are valid and one or more match the exclude pattern
				if (excludes != null) {
					rules.append(" exclude: ");
					for (Assertion ass : excludes) {
						ExpressionBinaryOperator ebo = (ExpressionBinaryOperator) ass.getExpression();
						ExpressionLeaf leftOperator = (ExpressionLeaf) ebo.getLeftOperand();
						if (leftOperator.getType().equalsIgnoreCase(ExpressionLeaf.STRING)) {
							String leftItem = (String) leftOperator.getItem();
							if (leftItem.matches("archetype_id/value")
								&& ebo.getOperator().equals(OperatorKind.OP_MATCHES)) {

								if (ebo.getRightOperand() != null) {
									String exclude =
											ebo.getRightOperand().toString()
												.substring(1, ebo.getRightOperand().toString().length() - 1);

									if (!exclude.equals(".*")) {
										rules.append(exclude);
										files.removeAll(getAdlFilesWithRegex(exclude));
									}
								}
							}
						}
					}
				}

				// if we have only one correct adl file, use that.
				if (files.size() == 1) {
					log.info(PathUtils.level + ": Use '" + files.get(0).getName() + "' for archetype-slot with rules: "
								+ rules.toString());

					File adlFile = files.get(0);

					includeArchetypeSlot(adlFile, ca, parent);

				} else if (files.size() == 0) {

					log.error("No matching archetypes found for archetype-slot with rules: " + rules.toString());

				} else {

					log.info("Archetype-slot with rules: " + rules.toString());

					StringBuilder availableFiles = new StringBuilder();

					availableFiles.append(0);
					availableFiles.append(": ");
					availableFiles.append("Do not include");
					availableFiles.append("\n");

					int i = 0;
					for (i = 0; i < files.size(); i++) {

						File file = files.get(i);
						availableFiles.append(i + 1);
						availableFiles.append(": ");
						availableFiles.append(file.getName());
						availableFiles.append("\n");

					}

					Integer in = null;
					while (in == null) {

						String input = UserInterfaceProvider.getInput(availableFiles.toString());

						try {

							in = Integer.valueOf(input);

							if (in < 0 || in > files.size()) {
								UserInterfaceProvider.printOutput("Please enter a valid number!");
								in = null;
							}

						} catch (NumberFormatException e) {
							UserInterfaceProvider.printOutput("Please enter a valid number!");
						}
					}

					if (in == 0) {
						log.info("Don't include archetype slot");
					} else {
						includeArchetypeSlot(files.get(in - 1), ca, parent);
					}
				}

			} else {

				// ConstraintRef: constraints on ontology not handled.

			}
		}

		decreaseLevel();
	}

	/**
	 * Triggers the processCComplexObject with the parsed archetype from the adlFile.
	 * 
	 * @param adlFile the archetype slot adl file
	 * @param ca last processed {@link CAttribute}
	 * @param parent parent tree element
	 */
	private void includeArchetypeSlot(final File adlFile, final CAttribute ca, final TreeElement parent) {

		ADLParser parser;
		try {

			parser = new AdlParserWrapper(adlFile, true, true);
			Archetype archetype = parser.parse();
			PathUtils.archetypeLevel++;

			if (PathUtils.archetypeLevel <= SetProperties.maxNumberArchetypeLevels) {

				processCComplexObject(ca, archetype.getDefinition(), parent);

			} else {

				log.error("ArchetypeSlot-Level exceeds maximum! Maybe a recurrence in the archetypes?");

			}

			PathUtils.archetypeLevel--;

		} catch (Exception e) {

			log.error("Error getting archetype-slot", e);

		}

	}

	/**
	 * Increase PathUtils.level of archetype.
	 */
	public void increaseLevel() {
		PathUtils.level++;
	}

	/**
	 * Decrease PathUtils.level of archetype.
	 */
	public void decreaseLevel() {
		PathUtils.level--;
	}

	/**
	 * @param regex regex to match the adl file with
	 * @return list of files that match the regex
	 */
	public List<File> getAdlFilesWithRegex(final String regex) {
		List<File> list = new ArrayList<File>();
		for (File f : adlFiles) {
			String filename = f.getName();
			String filenameWOext = filename.substring(0, StringUtils.lastIndexOf(filename, "."));
			if (filenameWOext.matches(regex)) {
				list.add(f);
			}
		}
		return list;
	}

	/**
	 * Process constraints on primitive objects like string, integer, ... and
	 * create a new tree element, which will be added to the list of children
	 * from the parent tree element.
	 * 
	 * @param cpo {@link CPrimitiveObject}
	 * @param ca {@link CAttribute}
	 * @param parent parent tree element
	 */
	private void processCPrimitiveObject(final CAttribute ca, final CPrimitiveObject cpo, final TreeElement parent) {

		new TreeElement(parent, ca, cpo, PathUtils.level);

	}

	/**
	 * Recursive method to process all tree elements, and add the context.
	 * 
	 * @param e tree element
	 */
	private void processTreeElement(final TreeElement e) {

		e.processContextElements();

		for (TreeElement te : e.getChildren()) {

			processTreeElement(te);

		}

	}

	/**
	 * Recursive method to traverse tree elements, and create the tests for the constraints.
	 * 
	 * @param e tree element
	 */
	private void processTestGeneration(final TreeElement e) {

		TestGenerator.generate(e);

		for (TreeElement te : e.getChildren()) {

			processTestGeneration(te);

		}
	}
}

package at.ac.meduniwien.mias.adltoschematron.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import lombok.extern.log4j.Log4j;
import at.ac.meduniwien.mias.adltoschematron.AdlToSchematronConverter;

@Log4j
public final class Reporter {

	private Reporter() {
	}

	/**
	 * read report and disply some stats.
	 * 
	 * @return
	 */
	public static int printReportToConsole() {
		int linenumber = 0;
		int failedAsserts = 0;
		int firedRule = 0;

		try {

			FileReader fr = new FileReader(new File(AdlToSchematronConverter.reportOutputFile));
			BufferedReader br = new BufferedReader(fr);

			String line;
			while ((line = br.readLine()) != null) {

				if (line.contains("<svrl:failed-assert")) {
					failedAsserts++;
				}
				if (line.contains("<svrl:fired-rule")) {
					firedRule++;
				}

				linenumber++;
			}

			log.info("number of lines in report:   " + String.format("%5d", linenumber));
			log.info("number of failed assertions: " + String.format("%5d", failedAsserts));
			log.info("number of fired rules:       " + String.format("%5d", firedRule));

			br.close();
			fr.close();
		} catch (Exception e) {

			log.error(e.getLocalizedMessage(), e);

		}

		return failedAsserts;
	}
}

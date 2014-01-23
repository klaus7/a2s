package at.ac.meduniwien.mias.adltoschematron.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import lombok.extern.log4j.Log4j;

/**
 * Provides the interface to the user.
 * 
 * @author Klaus Pfeiffer
 */
@Log4j
public final class UserInterfaceProvider {

	/**
	 * Hidden constructor.
	 */
	private UserInterfaceProvider() {
	}

	/**
	 * Get input from the user.
	 * 
	 * @param msg displayed message to the user
	 * @return input
	 */
	public static String getInput(final String msg) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print(msg + ":");
		String s = "";
		try {
			s = br.readLine();
		} catch (IOException e) {
			log.error("IOException", e);
		}
		return s;
	}

	/**
	 * Print output.
	 * 
	 * @param string string to print
	 */
	public static void printOutput(final String string) {
		System.out.print(string);
	}

}

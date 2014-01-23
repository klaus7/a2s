package at.ac.meduniwien.mias.adltoschematron.helpers;

/**
 * The assertion message builder class. Named short for convenient use.
 * 
 * @author Klaus
 * 
 */
public final class Msg implements IConstants {

	/**
	 * String builder for the message.
	 */
	private static StringBuilder message = new StringBuilder();

	/**
	 * Hide constructor.
	 */
	private Msg() {
	}

	/**
	 * Clear messages.
	 */
	public static void clear() {
		message = new StringBuilder();
	}

	/**
	 * Append new message.
	 * 
	 * @param msg message to add
	 */
	public static void add(final String msg) {
		//        Msg.message.append("(" + msg + ") or ");
		Msg.message.append(msg);
	}

	/**
	 * Set new message.
	 * 
	 * @param msg message to set
	 */
	public static void set(final String msg) {
		message = new StringBuilder(msg);
	}

	/**
	 * Get message and clear it afterwards.
	 * 
	 * @return message
	 */
	public static String get() {
		if (message.toString().endsWith(" or ")) {
			message.delete(message.length() - 4, message.length());
		}
		String s = message.toString();
		clear();
		return s;
	}

}

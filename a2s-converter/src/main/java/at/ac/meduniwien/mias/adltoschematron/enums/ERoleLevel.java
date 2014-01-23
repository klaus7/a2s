package at.ac.meduniwien.mias.adltoschematron.enums;

/**
 * Defines the role of an assertion.
 * 
 * @author Klaus Pfeiffer
 */
public enum ERoleLevel {
	Info("info", 3),
	Warning("warning", 5),
	Error("error", 10);

	/**
	 * Value of role leve.
	 */
	private String value;

	/**
	 * Severity of created assertion with this role level.
	 */
	private int severity;

	/**
	 * @param value {@link #value}
	 * @param severity {@link #severity}
	 */
	private ERoleLevel(final String value, final int severity) {
		this.value = value;
		this.severity = severity;
	}

	/**
	 * @param severity
	 * @return true if this role level's severity is greater or equal to the argument severity
	 */
	public boolean include(final int severity) {
		if (this.severity >= severity) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return value;
	}
}

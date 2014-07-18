package kasperimpl.converter.plugins.xdocreport;

import kasper.kernel.util.Assertion;

/**
 * Formats de sortie supportés par Open Office.
 * @author pchretien, npiedeloup
 */
enum ConverterFormat {

	/**
	 * OpenOffice Text.
	 */
	ODT("application/vnd.oasis.opendocument.text"),

	/**
	 * Document PDF.
	 */
	PDF("application/pdf");

	private final String typeMime;

	/**
	 * Constructeur.
	 * @param typeMime Type mime associé
	 */
	private ConverterFormat(final String typeMime) {
		this.typeMime = typeMime;
	}

	/**
	 * @return Type mime associé
	 */
	String getTypeMime() {
		return typeMime;
	}

	/**
	 * Récupère le Format associé à ce code de format.
	 * @param sFormat code de format (non null et doit être en majuscule)
	 * @return Format associé.
	 */
	static ConverterFormat find(final String sFormat) {
		Assertion.notNull(sFormat);
		Assertion.precondition(sFormat.equals(sFormat.trim().toUpperCase()), "Le format doit être en majuscule, et sans espace");
		// ---------------------------------------------------------------------
		final ConverterFormat format = valueOf(sFormat);
		return format;
	}
}

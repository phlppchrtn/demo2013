package kasperimpl.converter.plugins.xdocreport;

import kasper.kernel.util.Assertion;

/**
 * Formats de sortie support�s par Open Office.
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
	 * @param typeMime Type mime associ�
	 */
	private ConverterFormat(final String typeMime) {
		this.typeMime = typeMime;
	}

	/**
	 * @return Type mime associ�
	 */
	String getTypeMime() {
		return typeMime;
	}

	/**
	 * R�cup�re le Format associ� � ce code de format.
	 * @param sFormat code de format (non null et doit �tre en majuscule)
	 * @return Format associ�.
	 */
	static ConverterFormat find(final String sFormat) {
		Assertion.notNull(sFormat);
		Assertion.precondition(sFormat.equals(sFormat.trim().toUpperCase()), "Le format doit �tre en majuscule, et sans espace");
		// ---------------------------------------------------------------------
		final ConverterFormat format = valueOf(sFormat);
		return format;
	}
}

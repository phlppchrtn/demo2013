package kasperimpl.converter.plugins.xdocreport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.inject.Inject;

import kasper.file.FileManager;
import kasper.file.model.KFile;
import kasper.kernel.exception.KRuntimeException;
import kasper.kernel.util.Assertion;
import kasperimpl.converter.ConverterPlugin;
import kasperimpl.util.TempFile;

import org.apache.log4j.Logger;
import org.odftoolkit.odfdom.converter.pdf.PdfConverter;
import org.odftoolkit.odfdom.converter.pdf.PdfOptions;
import org.odftoolkit.odfdom.doc.OdfDocument;

/**
 * Conversion des fichiers à partir de XDocReport et son module org.odftoolkit.odfdom.converter.pdf.PdfConverter.
 * A ajouter dans le pom.xml
 *	<dependency>
 *		<groupId>fr.opensagres.xdocreport</groupId>
 *		<artifactId>org.odftoolkit.odfdom.converter.pdf</artifactId>
 *		<version>1.0.0</version>
 *		<optional>true</optional>
 *	</dependency>
 * @author npiedeloup
 * @version $Id: XDocReportConverterPlugin.java,v 1.1 2013/01/03 13:11:39 npiedeloup Exp $
 */
public final class XDocReportConverterPlugin implements ConverterPlugin {
	private static final Logger LOGGER = Logger.getLogger(XDocReportConverterPlugin.class);
	private final FileManager fileManager;

	/**
	 * Constructeur.
	 * @param fileManager Manager de gestion des fichiers
	 */
	@Inject
	public XDocReportConverterPlugin(final FileManager fileManager) {
		Assertion.notNull(fileManager);
		//---------------------------------------------------------------------
		this.fileManager = fileManager;
	}

	/** {@inheritDoc} */
	public final KFile convertToFormat(final KFile file, final String targetFormat) {
		Assertion.notEmpty(targetFormat);
		// ---------------------------------------------------------------------
		return convertToFormat(file, ConverterFormat.find(targetFormat));
	}

	private KFile convertToFormat(final KFile file, final ConverterFormat targetFormat) {
		Assertion.notNull(file);
		Assertion.notNull(targetFormat);
		// si le format de sortie est celui d'entrée la convertion est inutile
		Assertion.precondition(!targetFormat.getTypeMime().equals(file.getMimeType()), "Le format de sortie est identique à celui d'entrée ; la conversion est inutile");
		// ---------------------------------------------------------------------
		final File inputFile = fileManager.obtainReadOnlyFile(file);
		final File targetFile;
		try {
			targetFile = doConvertToFormat(inputFile, targetFormat);
		} catch (final Exception e) {
			throw new KRuntimeException("Erreur de conversion du document au format " + targetFormat.name(), e);
		}
		return fileManager.createFile(targetFile);

	}

	private File doConvertToFormat(final File file, final ConverterFormat targetFormat) throws Exception {
		// 1) Load odt with ODFDOM
		final OdfDocument document = OdfDocument.loadDocument(file);

		// 2) Convert ODFDOM OdfTextDocument 2 PDF with iText
		final File targetFile = new TempFile("edition", '.' + targetFormat.name());
		final OutputStream out = new FileOutputStream(targetFile);
		try {
			final PdfOptions options = PdfOptions.create().fontEncoding("windows-1250");
			PdfConverter.getInstance().convert(document, out, options);
			return targetFile;
		} finally {
			out.flush();
			out.close();
		}
	}
}

package kasperimpl.metadata.plugins.mp3;

import java.io.File;

import javax.inject.Inject;

import kasper.file.FileManager;
import kasper.file.model.KFile;
import kasper.kernel.util.Assertion;
import kasper.metadata.MetaDataContainer;
import kasper.metadata.MetaDataContainerBuilder;
import kasperimpl.metadata.MetaDataExtractorPlugin;
import de.ueberdosis.mp3info.ID3Reader;

/**
 * Gestion des documents de type mp3.
 *
 * @author pchretien
 * @version $Id: Mp3MetaDataExtractorPlugin.java,v 1.1 2012/11/14 09:12:46 pchretien Exp $
 */
public final class Mp3MetaDataExtractorPlugin implements MetaDataExtractorPlugin {
	private final FileManager fileManager;

	/**
	 * Constructeur.
	 * @param fileManager Manager de gestion des fichiers
	 */
	@Inject
	public Mp3MetaDataExtractorPlugin(final FileManager fileManager) {
		Assertion.notNull(fileManager);
		//---------------------------------------------------------------------
		this.fileManager = fileManager;
		de.ueberdosis.util.OutputCtr.setLevel(0);
	}

	/** {@inheritDoc} */
	public MetaDataContainer extractMetaData(final KFile file) throws Exception {
		Assertion.notNull(file);
		final File tmpFile = fileManager.obtainReadOnlyFile(file);
		//----------------------------------------------------------------------
		final ID3Reader reader = new ID3Reader(tmpFile.getAbsolutePath());
		final String album = reader.getExtendedID3Tag().getAlbum();
		final String title = reader.getExtendedID3Tag().getTitle();
		final String artist = reader.getExtendedID3Tag().getArtist();
		//----------------------------------------------------------------------
		return new MetaDataContainerBuilder()//
				.put(Mp3MetaData.ALBUM, album)//
				.put(Mp3MetaData.TITLE, title)//
				.put(Mp3MetaData.ARTIST, artist)//
				.build();
	}
}

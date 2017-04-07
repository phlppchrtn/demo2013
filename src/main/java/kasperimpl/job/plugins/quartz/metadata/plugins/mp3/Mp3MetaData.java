package kasperimpl.metadata.plugins.mp3;

import kasper.kernel.util.Assertion;
import kasper.metadata.MetaData;
import kasper.metadata.MetaDataType;

/**
 * Liste des m�tadonn�es pour les MP3.
 *
 * @author pchretien
 */
public enum Mp3MetaData implements MetaData {
	/** Album.*/
	ALBUM(MetaDataType.STRING),
	/** Title. */
	TITLE(MetaDataType.STRING),
	/**Artist.*/
	ARTIST(MetaDataType.STRING);

	//-------------------------------------------------------------------------
	private final MetaDataType metaDataType;

	/**
	 * Constructeur.
	 * Initialise la m�tadonn�e en lui donnant un type
	 * @param metaDataType	Type de la m�tadonn�e
	 */
	private Mp3MetaData(final MetaDataType metaDataType) {
		Assertion.notNull(metaDataType);
		//---------------------------------------------------------
		this.metaDataType = metaDataType;
	}

	/** {@inheritDoc} */
	public MetaDataType getType() {
		return metaDataType;
	}
}

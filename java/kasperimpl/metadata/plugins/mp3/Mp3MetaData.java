package kasperimpl.metadata.plugins.mp3;

import kasper.kernel.util.Assertion;
import kasper.metadata.MetaData;
import kasper.metadata.MetaDataType;

/**
 * Liste des m�tadonn�es pour les MP3.
 *
 * @author pchretien
 * @version $Id: Mp3MetaData.java,v 1.1 2012/11/14 09:12:46 pchretien Exp $
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

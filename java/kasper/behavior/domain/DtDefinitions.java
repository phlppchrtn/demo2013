package kasper.behavior.domain;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Attention cette classe est g�n�r�e automatiquement !
 */
public final class DtDefinitions implements Iterable<Class<?>> {
	public Iterator<Class<?>> iterator() {
		return Arrays.asList(new Class<?>[] { //
				Comment.class, Rating.class }).iterator();
	}
}
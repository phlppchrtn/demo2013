package kasper.jsf.flow;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author prahmoune
 * @version $Id: Flow.java,v 1.1 2012/12/04 12:35:33 pchretien Exp $
 */
@Target( { ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Flow {
	/**
	 * pas de contexte par défaut.
	 */
	String NONE = "[none]";

	/**
	 * @return context à supprimer
	 */
	String clear() default NONE;

}

package kasperimpl.jsf.tab;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import kasper.kernel.exception.KRuntimeException;
import kasper.kernel.util.Assertion;

import org.primefaces.event.TabChangeEvent;

/**
 * Managed Bean JSF pour conserver l'état (active tab) du composant primefaces TabView.
 * 
 * Déclaration d'un composant tabView dans un document xhtml:
 * <p:tabView activeIndex="#{tabViewState.index}" tabChangeListener="#{tabViewState.activeTabChanged}">    
 *   	<p:tab id="tab_0" title="Form">  
 *   		<ui:include src="testForm.xhtml"/> 
 *   	</p:tab>
 * </p:tabView>   
 * Note: les identifiants des composants tab 'id' doivent être préfixés par 'tab_'
 * @author prahmoune
 * @version $Id: TabViewState.java,v 1.1 2012/12/05 14:39:33 pchretien Exp $ 
 *
 */
@ViewScoped
@ManagedBean(name = "tabViewState")
public final class TabViewState implements Serializable {
	private static final long serialVersionUID = 4223025162786803646L;
	private static final String TAB_ID_PREFIX = "tab_";

	private int index;

	/**
	 * Constructeur.
	 */
	public TabViewState() {
		index = 0;
	}

	/**
	 * @return l'index du tab actif.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Setter pour l'index de l'état actif.
	 * @param index de l'état actif
	 */
	public void setIndex(final int index) {
		this.index = index;
	}

	/**
	 * Call back server indiquant que le tab actif a changé
	 * @param event event contenant le nouveau tab actif.
	 */
	public void activeTabChanged(final TabChangeEvent event) {
		final String id = event.getTab().getId();
		Assertion.notEmpty(id);
		// ---------------------------------------------------------------------
		if (!id.startsWith(TAB_ID_PREFIX)) {
			throw new KRuntimeException("L'identifiant du tab id='" + id + "' doit être préfixé par '" + TAB_ID_PREFIX + "'.");
		}
		index = Integer.parseInt(id.substring(TAB_ID_PREFIX.length()));
	}

}

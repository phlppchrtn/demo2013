package kasperimpl.jsf.tree;

import org.primefaces.model.TreeNode;

/**
 * API de chargement des KTreeNodes. 
 * Utilis�e pour faire du chargement des noeuds en Lazy loading.
 * L'impl�mentation doit avoir un constructeur public sans param�tre.
 * @author prahmoune
 * @version $Id: KTreeNodeLoader.java,v 1.1 2012/12/05 14:39:33 pchretien Exp $
 */
public interface KTreeNodeLoader {

	/**
	 * Charge les enfants d'un noeud (met � jour le noeud).
	 * @param parent Parent.
	 */
	void loadChildren(TreeNode parent);

}

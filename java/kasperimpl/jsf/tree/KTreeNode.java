package kasperimpl.jsf.tree;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import kasper.kernel.util.ClassUtil;

import org.primefaces.model.TreeNode;

/**
 * Type «KTreeNode».
 */
public final class KTreeNode implements TreeNode, Serializable {

	/** Default node type. */
	public static final String DEFAULT_TYPE = "default";

	/** Field «serialVersionUID». */
	private static final long serialVersionUID = 1645685485061248932L;

	private String type;
	private Object data;

	private TreeNode parent;
	private List<TreeNode> children;

	private boolean expanded;
	private boolean selectable = true;
	private boolean selected;
	private boolean leaf;

	private transient KTreeNodeLoader loader;

	/**
	 * Create a new instance of KTreeNode.
	 * 
	 * @param data Node data.
	 */
	public KTreeNode(final Object data) {
		type = DEFAULT_TYPE;
		this.data = data;
		children = new ArrayList<TreeNode>();
		leaf = true;
	}

	/**
	 * Create a new instance of KTreeNode.
	 * 
	 * @param data Node data.
	 * @param loader Node loader.
	 * @param leaf Leaf node.
	 */
	public KTreeNode(final Object data, final KTreeNodeLoader loader, final boolean leaf) {
		type = DEFAULT_TYPE;
		this.data = data;
		children = null;
		this.loader = loader;
		this.leaf = leaf;
	}

	/**
	 * @return «type» value.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Set «type».
	 * 
	 * @param type new value of «type».
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * @return «data» value.
	 */
	public Object getData() {
		return data;
	}

	/**
	 * Set «data».
	 * 
	 * @param data new value of «data».
	 */
	public void setData(final Object data) {
		this.data = data;
	}

	/**
	 * @return «parent» value.
	 */
	public TreeNode getParent() {
		return parent;
	}

	/**
	 * Set «parent».
	 * 
	 * @param parent new value of «parent».
	 */
	public void setParent(final TreeNode parent) {
		this.parent = parent;
	}

	/** {@inheritDoc} */
	public List<TreeNode> getChildren() {
		if (loader != null && children == null) {
			children = new ArrayList<TreeNode>();
			if (!isLeaf()) {
				loader.loadChildren(this);
			}

		}
		return children;
	}

	/**
	 * Set the children list.
	 * 
	 * @param children New cildren list.
	 */
	public void setChildren(final List<TreeNode> children) {
		if (children == null) {
			this.children = null;
		} else {
			this.children = new ArrayList<TreeNode>(children.size());
			for (final TreeNode child : children) {
				if (child != null) {
					addChild(child);
				}
			}
		}
	}

	/**
	 * Ajout un nouveau noeud fils.
	 * @param treeNode nouveau fils
	 */
	public void addChild(final TreeNode treeNode) {
		treeNode.setParent(this);
		if (children == null) {
			children = new ArrayList<TreeNode>();
		}
		children.add(treeNode);
		leaf = false;
	}

	/** {@inheritDoc} */
	public int getChildCount() {
		return getChildren().size();
	}

	/** {@inheritDoc} */
	public boolean isLeaf() {
		return leaf;
	}

	/** {@inheritDoc} */
	public boolean isExpanded() {
		return expanded;
	}

	/** {@inheritDoc} */
	public void setExpanded(final boolean expanded) {
		setExpanded(expanded, true);
	}

	/**
	 * Set expanded and propagate the expantion state to parent nodes.
	 * 
	 * @param expand New expand state.
	 * @param propagate Propagation flag.
	 */
	public void setExpanded(final boolean expand, final boolean propagate) {
		// Correction par rapport au DefaultTreeNode de primefaces
		// ajout de la possibilité de propager ou pas au parent l'expanded 
		expanded = expand;
		if (children == null && loader != null) {
			loader.loadChildren(this);
		}

		if (parent != null && propagate) {
			parent.setExpanded(expanded);
		}
	}

	/**
	 * @return «selectable» value.
	 */
	public boolean isSelectable() {
		return selectable;
	}

	/**
	 * Set «selectable».
	 * 
	 * @param selectable new value of «selectable».
	 */
	public void setSelectable(final boolean selectable) {
		this.selectable = selectable;
	}

	/**
	 * @return «selected» value.
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Set «selected».
	 * 
	 * @param selected new value of «selected».
	 */
	public void setSelected(final boolean selected) {
		this.selected = selected;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (data == null ? 0 : data.hashCode());
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		final KTreeNode other = (KTreeNode) obj;
		if (data == null) {
			if (other.data != null) {
				return false;
			}
		} else if (!data.equals(other.data)) {
			return false;
		}

		return true;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return data == null ? "KTreeNode()" : "KTreeNode(" + data.toString() + ")";
	}

	private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
		s.defaultReadObject();
		final String loaderName = (String) s.readObject();

		if (parent instanceof KTreeNode) {
			loader = ((KTreeNode) parent).loader;
		}
		if (loader == null && loaderName != null) {
			//
			loader = ClassUtil.newInstance(loaderName, KTreeNodeLoader.class);
		}
	}

	private void writeObject(final ObjectOutputStream s) throws IOException {
		s.defaultWriteObject();
		if (loader == null) {
			s.writeObject(null);
		} else {
			s.writeObject(loader.getClass().getName());
		}
	}
}

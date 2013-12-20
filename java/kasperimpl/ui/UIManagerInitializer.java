package kasperimpl.ui;

import java.util.ArrayList;
import java.util.List;

import kasper.kernel.manager.ManagerInitializer;
import kasper.ui.UIManager;
import kasper.ui.metamodel.UIDataType;
import kasper.ui.model.UIComponentType;
import kasper.ui.model.UIPropertyType;
import kasperimpl.ui.model.components.UIForm;
import kasperimpl.ui.model.components.UITable;
import kasperimpl.ui.model.types.UIComponentTypeNames;

/**
 * Initializer des types de composants.
 * 
 * TODO: - Supprimer la syntax wiki du model et ajouter
 * 	le support pour des types de données tels que les listes, map etc...
 *       - Non extensible par l'application client pour l'instant 
 *        => idées: - plusieurs init class pour les managers
 *                  ou - héritage de init class?
 * 
 * @author prahmoune
 *
 */
public class UIManagerInitializer implements ManagerInitializer<UIManager> {

	/** {@inheritDoc} */
	public void init(final UIManager uiManager) {
		registerTypes(uiManager);
		registerComponents(uiManager);
	}

	private void registerTypes(final UIManager uiManager) {
		registerFormType(uiManager);
		registerTableType(uiManager);
	}

	/**
	 * Méthode à sucharger pour l'enregistrement des composants graphiques
	 * 
	 * @param uiManager UIManager le manger du model
	 */
	protected void registerComponents(final UIManager uiManager) {
		//
	}

	private void registerFormType(final UIManager uiManager) {
		final List<UIPropertyType> propertyTypes = new ArrayList<UIPropertyType>();
		final UIPropertyType entityDtDefinition_pt = new UIPropertyType(UIForm.entityDtDefinition.name(), UIDataType.String, null);
		propertyTypes.add(entityDtDefinition_pt);
		final UIPropertyType createModeDefinition_pt = new UIPropertyType(UIForm.createModeDefinition.name(), UIDataType.String, null);
		propertyTypes.add(createModeDefinition_pt);
		final UIPropertyType readModeDefinition_pt = new UIPropertyType(UIForm.readModeDefinition.name(), UIDataType.String, null);
		propertyTypes.add(readModeDefinition_pt);
		final UIPropertyType updateModeDefinition_pt = new UIPropertyType(UIForm.updateModeDefinition.name(), UIDataType.String, null);
		propertyTypes.add(updateModeDefinition_pt);
		final UIComponentType uiComponentType = new UIComponentType(UIComponentTypeNames.FORM.name(), propertyTypes);
		uiManager.getUIRegistry().register(uiComponentType);
	}

	private void registerTableType(final UIManager uiManager) {
		final List<UIPropertyType> propertyTypes = new ArrayList<UIPropertyType>();
		final UIPropertyType entityDtDefinition_pt = new UIPropertyType(UITable.entityDtDefinition.name(), UIDataType.String, null);
		propertyTypes.add(entityDtDefinition_pt);
		final UIPropertyType columnsDefinition_pt = new UIPropertyType(UITable.columnsDefinition.name(), UIDataType.String, null);
		propertyTypes.add(columnsDefinition_pt);
		final UIComponentType uiComponentType = new UIComponentType(UIComponentTypeNames.TABLE.name(), propertyTypes);
		uiManager.getUIRegistry().register(uiComponentType);
	}

}

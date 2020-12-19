package de.whiletrue.toolsmod.gui.config.sub.modules;

import de.whiletrue.toolsmod.gui.config.ConfigGui;
import de.whiletrue.toolsmod.gui.widgets.TmTextfield;
import de.whiletrue.toolsmod.gui.widgets.rounding.listmultirow.MultirowListView;
import de.whiletrue.toolsmod.mod.Toolsmod;

public class ConfigGuiModules extends ConfigGui{

	//Search field
	private TmTextfield searchField = new TmTextfield(0, 0, 0, 0, "", txt->this.moduleList.updateValidation())
			.setPresetStringByKey("gui.config.modules.search");

	//Module list
	private MultirowListView<ModuleView> moduleList = new MultirowListView<ModuleView>(0, 0, 0, 0)
			.setListFormatting(0, 5, 1, 40)
			.setScrollStrength(20)
			.setBackground(0x50000000)
			.setValidator(m->m.isValid(this.searchField.getText()))
			.setItems(
				Toolsmod.getInstance().getModuleManager().getModules().stream().map(ModuleView::new).toArray(ModuleView[]::new)
			);
	
	
	public ConfigGuiModules() {
		super("gui.config.nav.modules");
	}

	@Override
	protected void init() {
		super.init();
		
		//Adds the listview
		this.addWidget(this.moduleList);
		
		//Positions the list view
		this.moduleList.move(
			this.width/2-100,
			this.startY+40,
			200,
			this.height-this.startY-40
		);
		
		//Positions the search field
		this.searchField.move(
			this.width/2-90,
			this.startY+12,
			180,
			16
		);
		
		//Adds the text search field
		this.addWidget(this.searchField);
	}
	
	@Override
	public void onClose() {
		//Saves the module settings
		Toolsmod.getInstance().getModuleManager().save();
		super.onClose();
	}
	
}

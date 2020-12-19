package de.whiletrue.toolsmod.gui.config.sub.commands;

import de.whiletrue.toolsmod.gui.config.ConfigGui;
import de.whiletrue.toolsmod.gui.widgets.TmTextfield;
import de.whiletrue.toolsmod.gui.widgets.rounding.listmultirow.MultirowListView;
import de.whiletrue.toolsmod.mod.Toolsmod;

public class ConfigGuiCommands extends ConfigGui{

	//Search field
	private TmTextfield searchField = new TmTextfield(0, 0, 0, 0, "", txt->this.settingsList.updateValidation())
			.setPresetStringByKey("gui.config.commands.search");

	//List with all settings
	private MultirowListView<CommandView> settingsList = new MultirowListView<CommandView>(0,0,0,0)
			.setListFormatting(0, 5, 1, 30)
			.setScrollStrength(5)
			.setBackground(0x50000000)
			.setItems(
				Toolsmod.getInstance().getCommandManager().getCommands().stream().map(CommandView::new).toArray(CommandView[]::new)
			).setValidator(i->i.isValid(this.searchField.getText()));
	
	public ConfigGuiCommands() {
		super("gui.config.nav.commands");
	}
	
	@Override
	protected void init() {
		super.init();
		
		//Adds the listview
		this.addWidget(this.settingsList);
		
		//Positions the list view
		this.settingsList.move(
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
		//Saves all changes
		Toolsmod.getInstance().getCommandManager().save();
		super.onClose();
	}

}

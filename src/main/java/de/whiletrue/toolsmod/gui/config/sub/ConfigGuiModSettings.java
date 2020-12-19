package de.whiletrue.toolsmod.gui.config.sub;

import de.whiletrue.toolsmod.gui.config.ConfigGui;
import de.whiletrue.toolsmod.gui.widgets.TmTextfield;
import de.whiletrue.toolsmod.gui.widgets.rounding.listscaleable.ScaleableListView;
import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.settings.views.SettingView;

public class ConfigGuiModSettings extends ConfigGui{

	//Search field
	private TmTextfield searchField = new TmTextfield(0, 0, 0, 0, "", txt->this.settingsList.updateValidation())
			.setPresetStringByKey("gui.config.modsettings.search");

	//List with all settings
	private ScaleableListView<SettingView<?>> settingsList = new ScaleableListView<SettingView<?>>(0,0,0,0)
			.setSpaceY(10)
			.setScrollStrength(20)
			.setValidator(i->i.isValid(this.searchField.getText()));
	
	public ConfigGuiModSettings() {
		super("gui.config.nav.modsettings");
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
		
		//Appends the settings to the list
		this.settingsList.setItems(
			//Gets all settings
			Toolsmod.getInstance().getSettingsManager().getSettings().stream()
			.filter(i->!i.isInvisible())//Filters the invisible ones
			.map(i->i.getView(null))//Map them to their widgets
			.toArray(SettingView[]::new)//Gets them
		);
		
		//Adds the text search field
		this.addWidget(this.searchField);
	}

	@Override
	public void closeScreen() {
		//Saves the settings
		Toolsmod.getInstance().getSettingsManager().save();
		super.closeScreen();
	}
	
}

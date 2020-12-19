package de.whiletrue.toolsmod.settings.views;

import de.whiletrue.toolsmod.gui.widgets.TmCheckbox;
import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.settings.defined.SettingBool;

public class SettingViewBool extends SettingView<SettingBool>{

	//Gui widget
	private TmCheckbox widget = new TmCheckbox(0, 0, this.item.value, i->this.item.value=i);
	
	public SettingViewBool(SettingBool item, Module mod) {
		super(item, mod);
		
		//Appends the widget
		this.widgets.add(this.widget);
	}
	
	@Override
	public int handleChangePosition(int x, int w) {
		//Updates the widget
		this.widget.setX(x);
		
		return super.handleChangePosition(x, w)+20;
	}
	
	@Override
	public void handleChangePositionScrolled(int y) {
		//Updates the widget
		this.widget.setY(y+10);
		
		super.handleChangePositionScrolled(y);
	}

}

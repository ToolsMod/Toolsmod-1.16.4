package de.whiletrue.toolsmod.settings.views;

import de.whiletrue.toolsmod.gui.widgets.TmKeybindButton;
import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.settings.defined.SettingKeybind;

public class SettingViewKeybind extends SettingView<SettingKeybind>{

	//Widget
	private TmKeybindButton widget = new TmKeybindButton(0, 0, 80, 20, this.item.value, i->{});
	
	public SettingViewKeybind(SettingKeybind item, Module mod) {
		super(item, mod);
		//Adds the widget
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

package de.whiletrue.toolsmod.settings.views;

import de.whiletrue.toolsmod.gui.widgets.TmTextfield;
import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.settings.defined.SettingInteger;

public class SettingViewIntegerTextfield extends SettingView<SettingInteger> {

	//The slider widget
	private TmTextfield widget = (TmTextfield) new TmTextfield()
	//Only allows floats or empty values
	.setValidator(i->i.isEmpty() || this.item.handleParse(i))
	.setText(this.item.handleSave())
	.setHeight(15);
	
	public SettingViewIntegerTextfield(SettingInteger item, Module mod) {
		super(item, mod);
		
		//Adds the widget
		this.widgets.add(this.widget);
	}

	@Override
	public int handleChangePosition(int x, int w) {
		//Updates the slider
		this.widget.setX(x+1).setWidth(w-2);
		
		return super.handleChangePosition(x, w)+17;
	}
	
	@Override
	public void handleChangePositionScrolled(int y) {
		//Updates the widget
		this.widget.setY(y+11);
		
		super.handleChangePositionScrolled(y);
	}
	
}

package de.whiletrue.toolsmod.settings.views;

import de.whiletrue.toolsmod.gui.widgets.TmSlider;
import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.settings.defined.SettingInteger;

public class SettingViewIntegerSlider extends SettingView<SettingInteger>{

	//The slider widget
	private TmSlider slider = new TmSlider(0, 0, 0, 15, this.item.getMin(), this.item.getMax(), this.item.value, (s,v)->{
		this.item.value=(int)v;
		return this.item.value+this.item.getSuffix();
	})
	.setByValue(this.item.value);
	
	public SettingViewIntegerSlider(SettingInteger item, Module mod) {
		super(item, mod);
		//Appends the widget
		this.widgets.add(this.slider);
	}
	
	@Override
	public int handleChangePosition(int x, int w) {
		//Updates the slider
		this.slider.setX(x).setWidth(w);
		
		return super.handleChangePosition(x, w)+15;
	}
	@Override
	public void handleChangePositionScrolled(int y) {
		super.handleChangePositionScrolled(y);
		//Updates the slider
		this.slider.setY(y+10);
	}

}

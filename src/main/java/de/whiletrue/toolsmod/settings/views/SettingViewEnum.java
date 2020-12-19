package de.whiletrue.toolsmod.settings.views;

import de.whiletrue.toolsmod.gui.widgets.TmSliderValues;
import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.settings.defined.SettingEnum;

public class SettingViewEnum<X extends Enum<?>> extends SettingView<SettingEnum<X>>{

	//Gui-widget
	@SuppressWarnings("unchecked")
	private TmSliderValues<X> sSlider = (TmSliderValues<X>) new TmSliderValues<X>(
			0, 0, 0, 0,
			//Gets all enum values
			(X[]) this.item.value.getDeclaringClass().getEnumConstants(),
			0,
			(val,index)->{
				//Updates the value
				this.item.value=val;
				//Sets the new display
				return this.item.getOnDisplay().apply(val);
			}
		)
		.setStateByValue(this.item.value)
		.setHeight(15);
	
	public SettingViewEnum(SettingEnum<X> item, Module mod) {
		super(item, mod);
		
		//Appends the widget
		this.widgets.add(this.sSlider);
	}

	@Override
	public int handleChangePosition(int x, int w) {
		//Updates the item
		this.sSlider.setX(x).setWidth(w);
		
		return super.handleChangePosition(x, w)+15;
	}
	@Override
	public void handleChangePositionScrolled(int y) {
		//Updates the item
		this.sSlider.setY(y+10);
		
		super.handleChangePositionScrolled(y);
	}
	
}

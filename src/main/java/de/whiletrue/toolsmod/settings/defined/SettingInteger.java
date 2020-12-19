package de.whiletrue.toolsmod.settings.defined;

import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.settings.Setting;
import de.whiletrue.toolsmod.settings.views.SettingView;
import de.whiletrue.toolsmod.settings.views.SettingViewIntegerTextfield;
import de.whiletrue.toolsmod.settings.views.SettingViewIntegerSlider;

public class SettingInteger extends Setting<Integer>{
	
	//Max and min value of the setting
	private Float max,min;
	
	//If the widget is a slider and which suffix should be used
	private String suffix;
	
	public SettingInteger max(float value) {
		this.max=value;
		return this;
	}
	public SettingInteger min(float value) {
		this.min=value;
		return this;
	}
	public SettingInteger slider(String suffix) {
		this.suffix=suffix;
		return this;
	}
	
	@Override
	public String handleSave() {
		return super.value.toString();
	}

	@Override
	public boolean handleParse(String value) {
		try {
			//Parses the value
			Integer val = Integer.valueOf(value);
			
			//Checks if a max value is given and if the value is above that
			if(this.max!=null && val>this.max)
				return false;
			
			//Checks if a min value is given and if the value is above that
			if(this.min!=null && val<this.min)
				return false;
			
			//Update the value
			this.value=val;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Float getMin() {
		return this.min;
	}
	public Float getMax() {
		return this.max;
	}
	public String getSuffix() {
		return this.suffix;
	}
	@SuppressWarnings("unchecked")
	@Override
	public <X extends Setting<Integer>> SettingView<X> getView(Module mod) {
		//Checks which widget to use
		if(this.suffix!=null && this.min!=null && this.max != null)
			return (SettingView<X>) new SettingViewIntegerSlider(this, mod);
		return (SettingView<X>) new SettingViewIntegerTextfield(this, mod);
	}
}

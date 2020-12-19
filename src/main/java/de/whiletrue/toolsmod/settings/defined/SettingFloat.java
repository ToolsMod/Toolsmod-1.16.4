package de.whiletrue.toolsmod.settings.defined;

import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.settings.Setting;
import de.whiletrue.toolsmod.settings.views.SettingView;
import de.whiletrue.toolsmod.settings.views.SettingViewFloatTextfield;
import de.whiletrue.toolsmod.settings.views.SettingViewFloatSlider;

public class SettingFloat extends Setting<Float>{

	//Amount of decimal places
	private Integer maxDecimalPlaces;
	
	//Max and min value of the float
	private Float max,min;
	
	//If the widget is a slider and which suffix should be used
	private String suffix;
	
	public SettingFloat max(float value) {
		this.max=value;
		return this;
	}
	public SettingFloat min(float value) {
		this.min=value;
		return this;
	}
	public SettingFloat maxDecimalPlaces(int places) {
		this.maxDecimalPlaces=places;
		return this;
	}
	public SettingFloat slider(String suffix) {
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
			Float val = Float.valueOf(value);
			
			//Checks if a max value is given and if the value is above that
			if(this.max!=null && val>this.max)
				return false;
			
			//Checks if a min value is given and if the value is above that
			if(this.min!=null && val<this.min)
				return false;
			
			//Parses the value
			this.setValue(Float.valueOf(value));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Sets the value if it matches
	 * @param value the inserted value
	 */
	public void setValue(float value) {
		//Checks if the number should be rounded
		if(this.maxDecimalPlaces!=null) {
			//The amount of decimal places as 10er
			long pow = (long) Math.pow(10, this.maxDecimalPlaces);
			
			//Updates the value
			this.value=(float)((int)(value*pow))/(float)pow;
		}else
			//Updates the value
			this.value=value;
	}
	
	public Float getMax() {
		return this.max;
	}
	public Float getMin() {
		return this.min;
	}
	public Integer getMaxDecimalPlaces() {
		return this.maxDecimalPlaces;
	}
	public String getSuffix() {
		return this.suffix;
	}
	@SuppressWarnings("unchecked")
	@Override
	public <X extends Setting<Float>> SettingView<X> getView(Module mod) {
		//Checks which widget to use
		if(this.suffix!=null && this.min!=null && this.max != null)
			return (SettingView<X>) new SettingViewFloatSlider(this, mod);
		return (SettingView<X>) new SettingViewFloatTextfield(this, mod);
	}
}

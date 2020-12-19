package de.whiletrue.toolsmod.settings.defined;

import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.settings.Setting;
import de.whiletrue.toolsmod.settings.views.SettingView;
import de.whiletrue.toolsmod.settings.views.SettingViewBool;

public class SettingBool extends Setting<Boolean>{

	@Override
	public String handleSave(){
		return super.value.toString();
	}

	@Override
	public boolean handleParse(String value) {
		try {
			super.value = Boolean.valueOf(value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X extends Setting<Boolean>> SettingView<X> getView(Module mod) {
		return (SettingView<X>) new SettingViewBool(this, mod);
	}
}

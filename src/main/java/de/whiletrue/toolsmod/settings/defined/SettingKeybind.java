package de.whiletrue.toolsmod.settings.defined;

import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.settings.Setting;
import de.whiletrue.toolsmod.settings.views.SettingView;
import de.whiletrue.toolsmod.settings.views.SettingViewKeybind;
import de.whiletrue.toolsmod.util.Keybind;
import net.minecraftforge.client.settings.KeyModifier;

public class SettingKeybind extends Setting<Keybind>{

	@Override
	public String handleSave() {
		//Saves using the format (KEYCODE-MODIFIERS)
		return String.format("%d-%s",this.value.getKeyCode(),this.value.getModifiers());
	}

	@Override
	public boolean handleParse(String value) {
		try {
			//Splits into the segments
			String[] split = value.split("-");
			
			//Gets the key
			int key = Integer.valueOf(split[0]);
			
			//Gets the modifiers
			KeyModifier mod = KeyModifier.valueOf(split[1]);
			
			//Updates the keybind
			this.value.update(key, mod);
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X extends Setting<Keybind>> SettingView<X> getView(Module mod) {
		return (SettingView<X>) new SettingViewKeybind(this, mod);
	}
}

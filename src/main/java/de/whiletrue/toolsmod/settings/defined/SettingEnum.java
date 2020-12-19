package de.whiletrue.toolsmod.settings.defined;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.settings.Setting;
import de.whiletrue.toolsmod.settings.views.SettingView;
import de.whiletrue.toolsmod.settings.views.SettingViewEnum;

public class SettingEnum<Enm extends Enum<?>> extends Setting<Enm>{

	//Function to get the identity of the given enum
	private Function<Enm, String> onIdentify,
	//Function to get the display for an enum type
	onDisplay;
	
	public SettingEnum(Function<Enm, String> onIdentify,Function<Enm, String> onDisplay) {
		this.onIdentify=onIdentify;
		this.onDisplay=onDisplay;
	}
	
	@Override
	public String handleSave() {
		return this.onIdentify.apply(this.value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean handleParse(String value) {
		//Gets all value of the enum
		Optional<?> x = Arrays.stream(this.value.getDeclaringClass().getEnumConstants())
		//Filters for the right value
		.filter(i->this.onIdentify.apply((Enm) i).equals(value))
		//Gets the enum value
		.findFirst();
		
		//Updates the value if any got found
		x.ifPresent(i->this.value=(Enm) i);
		
		return x.isPresent();
	}

	public Function<Enm, String> getOnDisplay() {
		return this.onDisplay;
	}
	public Function<Enm, String> getOnIdentify() {
		return this.onIdentify;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X extends Setting<Enm>> SettingView<X> getView(Module mod) {
		return (SettingView<X>) new SettingViewEnum<Enm>(this, mod);
	}

}

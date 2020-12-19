package de.whiletrue.toolsmod.module.defined.tools.mapcreator;

import de.whiletrue.toolsmod.util.classes.TextUtil;

public enum EnumRenderSettings {

	ALL("all"),
	CORNERS("corners"),
	NEAR_ME("near_me"),
	NONE("none");
	
	//Translation
	private String key;
	
	private EnumRenderSettings(String key) {
		this.key=key;
	}
	
	public String getName() {
		return TextUtil.getInstance().getByKey("modules.mapcreator.render."+this.key);
	}
	
}

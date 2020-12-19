package de.whiletrue.toolsmod.module;

import de.whiletrue.toolsmod.util.classes.TextUtil;

public enum ModuleCategory {
	TOOLS("tools"),
	UTILS("utils"),
	VISUALS("visuals");

    //Category name
    private String name;

    private ModuleCategory(String name){
        this.name=name;
    }
    
    public String getKey() {
		return this.name;
	}

    public String getFormattedName() {
		return TextUtil.getInstance().getByKey("module.category."+this.name);
	}
}

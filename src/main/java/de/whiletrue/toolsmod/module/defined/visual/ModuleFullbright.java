package de.whiletrue.toolsmod.module.defined.visual;

import de.whiletrue.toolsmod.module.ModuleCategory;
import de.whiletrue.toolsmod.module.defined.Module;
import net.minecraftforge.event.TickEvent.ClientTickEvent;

public class ModuleFullbright extends Module{

	public ModuleFullbright() {
		super("Fullbright", ModuleCategory.VISUALS, true);
	}

	@Override
	public void onTick(ClientTickEvent event) {
		//Updates the gamma settings
		this.mc.gameSettings.gamma=1000;
	}
	
	@Override
	public void onDisable() {
		//Resets the gamma settings
		this.mc.gameSettings.gamma=1;
	}
	
	
}

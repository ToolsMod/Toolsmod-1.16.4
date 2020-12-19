package de.whiletrue.toolsmod.module.defined.utils;

import de.whiletrue.toolsmod.module.ModuleCategory;
import de.whiletrue.toolsmod.module.defined.Module;
import net.minecraftforge.event.TickEvent.ClientTickEvent;

public class ModuleFastplace extends Module{

	public ModuleFastplace() {
		super("Fastplace", ModuleCategory.UTILS, true);
	}
	
	@Override
	public void onTick(ClientTickEvent event) {
		//Resets the block click delay
		this.mc.rightClickDelayTimer=0;
	}

}

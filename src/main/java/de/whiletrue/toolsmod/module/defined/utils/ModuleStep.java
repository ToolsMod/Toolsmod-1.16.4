package de.whiletrue.toolsmod.module.defined.utils;

import de.whiletrue.toolsmod.module.ModuleCategory;
import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.settings.defined.SettingFloat;
import net.minecraftforge.event.TickEvent.ClientTickEvent;

public class ModuleStep extends Module{

	//Height the user can step
	private SettingFloat sHeight = new SettingFloat()
			.min(0)
			.max(10)
			.maxDecimalPlaces(1)
			.slider(" Blocks")
			.standard(3f)
			.name("height");
	
	public ModuleStep() {
		super("Step", ModuleCategory.UTILS, true);
	}
	
	@Override
    public void onDisable() {
        //Resets the step height
        this.mc.player.stepHeight=.6f;
    }

    @Override
    public void onTick(ClientTickEvent evt) {
    	//Sets the step height
    	this.mc.player.stepHeight=this.sHeight.value;
    }

}

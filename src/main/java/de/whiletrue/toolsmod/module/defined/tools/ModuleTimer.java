package de.whiletrue.toolsmod.module.defined.tools;

import de.whiletrue.toolsmod.module.ModuleCategory;
import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.settings.defined.SettingFloat;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedOutEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;

public class ModuleTimer extends Module{

	//Game speed with the timer enabled
	private SettingFloat sSpeed = new SettingFloat()
			.min(0.001f)
			.standard(5f)
			.name("speed");
	
	public ModuleTimer() {
		super("Timer",ModuleCategory.TOOLS,false);
	}
	
	@Override
	public void onTick(ClientTickEvent event) {
        //Updates the timer setting
        this.mc.timer.tickLength=50/this.sSpeed.value;
	}
	
	@Override
	public void onDisable() {
		//Resets the timer back to default
        this.mc.timer.tickLength=50;
	}
	
	@Override
	public void onDisconnect(LoggedOutEvent event) {
		this.disable();
	}

}

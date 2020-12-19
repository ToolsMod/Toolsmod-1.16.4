package de.whiletrue.toolsmod.module.defined.utils;

import de.whiletrue.toolsmod.module.ModuleCategory;
import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.settings.defined.SettingFloat;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.TickEvent.ClientTickEvent;

public class ModuleEntityFly extends Module{

	//Speed of the entity when flying
	private SettingFloat sSpeed = new SettingFloat()
			.name("speed")
			.standard(.5f);
	
	public ModuleEntityFly() {
		super("EntityFly", ModuleCategory.UTILS, true);
	}
	
	@Override
	public void onTick(ClientTickEvent event) {
		//Checks if the fly-key is pressed
        if(!this.mc.gameSettings.keyBindJump.isKeyDown())
            return;
        //Gets the riding entity
        Entity riding = this.mc.player.getRidingEntity();

        //Checks if the entity is existing
        if(riding==null)
            return;

        //Applies the motion to the entity
        riding.setMotion(0,this.sSpeed.value,0);
	}

}

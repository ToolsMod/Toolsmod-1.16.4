package de.whiletrue.toolsmod.module.defined.utils;

import de.whiletrue.toolsmod.module.ModuleCategory;
import de.whiletrue.toolsmod.module.defined.Module;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraftforge.event.TickEvent.ClientTickEvent;

public class ModuleNoFall extends Module{

	public ModuleNoFall() {
		super("Nofall", ModuleCategory.UTILS, true);
	}
	
	@Override
	public void onTick(ClientTickEvent event) {
		//Checks if the user has fallen longer that 2 blocks
        if(this.mc.player.fallDistance>2F)
            //Sends a ground-update packet
            this.mc.player.connection.sendPacket(new CPlayerPacket(true));
	}

}

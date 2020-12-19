package de.whiletrue.toolsmod.module.defined.utils;

import de.whiletrue.toolsmod.module.ModuleCategory;
import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.settings.defined.SettingFloat;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;

public class ModuleVelocity extends Module{

	//Amount of knockback on the x/z axis
	private SettingFloat sStrengthXZ = new SettingFloat()
			.name("strengthXZ")
			.standard(0f);
	
	//Amount of knockback on the y axis
	private SettingFloat sStrengthY = new SettingFloat()
			.name("strengthY")
			.standard(0f);
	
	public ModuleVelocity() {
		super("Velocity", ModuleCategory.UTILS, true);
	}
	
	@Override
	public boolean onServerPacket(IPacket<IClientPlayNetHandler> packet) {
		if(packet instanceof SEntityVelocityPacket) {
			SEntityVelocityPacket vp = (SEntityVelocityPacket) packet;
			
			//Checks if the entity is the player
			if(vp.getEntityID() != this.mc.player.getEntityId())
				return true;
			
			//Updates the velocity
			vp.motionX*=this.sStrengthXZ.value;
			vp.motionY*=this.sStrengthY.value;
			vp.motionZ*=this.sStrengthXZ.value;
		}
		return true;
	}	
}
package de.whiletrue.toolsmod.module.defined.tools;

import de.whiletrue.toolsmod.module.ModuleCategory;
import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.module.defined.tools.ghostsight.GhostPlayer;
import de.whiletrue.toolsmod.settings.defined.SettingFloat;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedOutEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;

public class ModuleGhostsight extends Module{

	//Speed of the ghost
	private SettingFloat sSpeed = new SettingFloat()
			.name("speed")
			.standard(0.05f);
	
	//Ghost-player
	private GhostPlayer ghost = new GhostPlayer();
	
	//Saved game type
	private GameType gametype;
	
	public ModuleGhostsight() {
		super("Ghostsight", ModuleCategory.TOOLS, false);
	}

	@Override
	public void onEnable() {
		//Creates the ghost
        this.ghost.create();

        //Pushes the player out of his body
        this.mc.player.addVelocity(0,.15,0);
        
        //Gets the player info
        NetworkPlayerInfo npi = this.mc.getConnection().getPlayerInfo(this.mc.player.getGameProfile().getId());
        //Saves the previous game type
        this.gametype=npi.gameType;
        //Updates the game type
        npi.gameType=GameType.SPECTATOR;
        
        //Grants the player fly abilites
        this.mc.player.abilities.isFlying=true;
	}
	
	@Override
	public void onTick(ClientTickEvent event) {
		//Updates the speed
		this.mc.player.abilities.setFlySpeed(this.sSpeed.value);
	}
	
	@Override
	public void onDisable() {
		//Gets the player info
		NetworkPlayerInfo npi = this.mc.getConnection().getPlayerInfo(this.mc.player.getGameProfile().getId());
		//Updates the game type
		npi.gameType=this.gametype;
		
		//Removes any motion from the player
		this.mc.player.setMotion(0,0,0);
		
        //Removes the ghost
        this.ghost.destroy(true);
	}
	
	@Override
	public void onDisconnect(LoggedOutEvent event) {
		this.disable();
	}
	
	@Override
	public boolean onClientPacket(IPacket<IServerPlayNetHandler> packet) {
		//Checks if the packet can be send
		return this.ghost.handlePacket(packet);
	}
	
}

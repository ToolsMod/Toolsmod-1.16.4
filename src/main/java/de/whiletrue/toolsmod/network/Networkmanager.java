package de.whiletrue.toolsmod.network;

import de.whiletrue.toolsmod.mod.Toolsmod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.IServerPlayNetHandler;

public class Networkmanager {

	//Reference to the networkmanager
	private static Networkmanager instance;
	
	private Networkmanager() {}
	
	public static Networkmanager getInstance() {
		if(instance==null)
			instance=new Networkmanager();
		return instance;
	}

	/**
	 * Fires once an packet from the client gets send
	 * @param packet the packet that got send
	 * @return if the packet should be send
	 */
	public boolean onClientPacket(IPacket<IServerPlayNetHandler> packet) {
		//Executes the event on every active module and checks if the packet should be send
		if(Toolsmod.getInstance().getModuleManager().getEnabledModules().stream().filter(i->!i.onClientPacket(packet))
		.findAny().isPresent())
			return false;
		
		return true;
	}
	
	/**
	 * Fires once an packet from the server gets received
	 * @param packet the packet that got received
	 * @return if the packet should be processed
	 */
	public boolean onServerPacket(IPacket<IClientPlayNetHandler> packet) {
		//Executes the event on every active module and checks if the packet should be send
		if(Toolsmod.getInstance().getModuleManager().getEnabledModules().stream().filter(i->!i.onServerPacket(packet))
		.findAny().isPresent())
			return false;
		return true;
	}
	
	/**
	 * Handler the server login event
	 */
	public void handleLogin() {
		Minecraft.getInstance().getConnection().getNetworkManager().channel().pipeline().addBefore("packet_handler", "PacketInjector", new PacketHandler());
	}
	
	
}

package de.whiletrue.toolsmod.network;


import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.IServerPlayNetHandler;

@SuppressWarnings("unchecked")
public class PacketHandler extends ChannelDuplexHandler{

	//Referene to the network manager
	private Networkmanager manager = Networkmanager.getInstance();
	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		//Casts the packet
		IPacket<IServerPlayNetHandler> packet = (IPacket<IServerPlayNetHandler>) msg;
		
		//Checks if the packet should be send
		if(this.manager.onClientPacket(packet))
			super.write(ctx, msg, promise);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		//Casts the packet
		IPacket<IClientPlayNetHandler> packet = (IPacket<IClientPlayNetHandler>) msg;
		
		//Checks if the packet should be processed
		if(this.manager.onServerPacket(packet))
			super.channelRead(ctx, msg);
	}
	
}

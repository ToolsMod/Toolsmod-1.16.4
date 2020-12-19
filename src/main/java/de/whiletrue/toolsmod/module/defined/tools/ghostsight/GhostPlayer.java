package de.whiletrue.toolsmod.module.defined.tools.ghostsight;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import de.whiletrue.toolsmod.mod.Toolsmod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.world.GameType;

public class GhostPlayer {

	//Reference to the game
	private Minecraft mc = Minecraft.getInstance();
	
	//Packets that should be blocked on the client side
    private static Class<?>[] BLOCKED_PACKETS = new Class<?>[]{
            CPlayerPacket.PositionPacket.class,
            CPlayerPacket.PositionRotationPacket.class,
            CPlayerPacket.RotationPacket.class,
            CEntityActionPacket.class,
            CAnimateHandPacket.class
    };
    
    //Ghost player
    public RemoteClientPlayerEntity ghost;
    
    /**
     * Creates the ghost player
     */
    public void create() {
    	//Destroys the ghost
    	this.destroy(false);
    	
    	//Creates the cloned profile
    	GameProfile p = new GameProfile(UUID.randomUUID(), String.format("§8[§%cGhostsight§8] §%c%s",Toolsmod.COLOR_MAIN,Toolsmod.COLOR_SECONDARY,this.mc.player.getName().getString()));
    	
    	//Creates the ghost
    	this.ghost=new RemoteClientPlayerEntity(this.mc.world, p);
    	this.ghost.setGameType(GameType.SURVIVAL);
    	this.ghost.setEntityId(-200);
    	//Applies the position
        this.ghost.copyLocationAndAnglesFrom(this.mc.player);
        this.ghost.rotationYawHead=this.mc.player.rotationYawHead;
        this.ghost.setOnGround(this.mc.player.isOnGround());
        //Sets the ghost sneaking if the player is
        this.ghost.setSneaking(this.mc.player.isSneaking());
        //Copies the inventory
        this.ghost.inventory.copyInventory(this.mc.player.inventory);
        //Sets the abilities
        CompoundNBT nbt = new CompoundNBT();
        this.mc.player.abilities.write(nbt);
        this.ghost.abilities.read(nbt);
        
        //Adds the ghost to the world
        this.mc.world.addEntity(this.ghost.getEntityId(),this.ghost);
    }
	
    /**
     * Destroys the ghost
     * @param tpBack if the player should be teleported back to him and be reset
     */
    public void destroy(boolean tpBack) {
    	 //Checks if the entity exists
        if(this.ghost==null)
            return;
        
        //Checks if the player should be setback
        if(tpBack){
            //Sets the player back to his original location
            this.mc.player.copyLocationAndAnglesFrom(this.ghost);
            this.mc.player.setRotationYawHead(this.ghost.getRotationYawHead());
            this.mc.player.setOnGround(this.ghost.isOnGround());
            //Sets the velocity
            this.mc.player.setMotion(this.ghost.getMotion());
            //Sets the abilities
            CompoundNBT nbt = new CompoundNBT();
            this.ghost.abilities.write(nbt);
            this.mc.player.abilities.read(nbt);
        }

        //Removes the entity from the world
        this.mc.world.removeEntityFromWorld(this.ghost.getEntityId());
        this.ghost=null;
    }
    
    /**
     * Handles the ghost packets
     * @return if the packet can be send
     */
    public boolean handlePacket(IPacket<IServerPlayNetHandler> packet) {
    	//Checks if the ghost exists
        if(this.ghost == null)
            return true;

        //Checks if the current packet is contained in the array of packets
        for(Class<?> c : BLOCKED_PACKETS)
            if(c.equals(packet.getClass()))
                //Should block the packet
                return false;

        return true;
    }
    
}

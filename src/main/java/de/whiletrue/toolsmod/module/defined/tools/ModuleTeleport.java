package de.whiletrue.toolsmod.module.defined.tools;

import de.whiletrue.toolsmod.module.ModuleCategory;
import de.whiletrue.toolsmod.module.defined.Module;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

public class ModuleTeleport extends Module{

	public ModuleTeleport() {
		super("Teleport", ModuleCategory.TOOLS, false);
	}

	@Override
    public void onEnable() {
        //Disables the module
        this.disable();

        //Gets the block the player is looking at (Distance 500)
        RayTraceResult block = this.blockPlayerLookingAt(500);

        //Gets the player
        ClientPlayerEntity p = this.mc.player;
        
        //Checks if the block was found
        if(block.getType().equals(RayTraceResult.Type.MISS))
            return;

        //Gets the coordinates
        double x = block.getHitVec().getX();
        double y = block.getHitVec().getY();
        double z = block.getHitVec().getZ();

        //Calculates the distance to that block
        double dist = p.getDistanceSq(x,y,z);

        //Iterates over every 2 steps to the distance
        for(double d = 0;d<dist;d+=2){
            //Teleport's the player
            this.setPosition(
        		p.getPosX() + (x - p.getHorizontalFacing().getXOffset() - p.getPosX()) * d / dist,
                p.getPosY() + (y - p.getPosY()) * d / dist,
                p.getPosZ() + (z - p.getHorizontalFacing().getZOffset() - p.getPosZ()) * d / dist
            );
        }
    }

    /**
     * Gets the block a player is looking at
     *
     * @param blockReachDistance the distance to search for
     * */
    private RayTraceResult blockPlayerLookingAt(double blockReachDistance) {
        //Gets the eye position
        Vector3d eyePos = this.mc.player.getEyePosition(1.0f);
        //Gets the rotation vector
        Vector3d rotation = this.mc.player.getLookVec();
        //Adds the rotation vector to the eye position * block distance
        Vector3d distance = eyePos.add(rotation.x * blockReachDistance, rotation.y * blockReachDistance,
                rotation.z * blockReachDistance);

        //Gets the content
        RayTraceContext cont = new RayTraceContext(eyePos, distance, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, this.mc.player);

        //Returns the result
        return this.mc.world.rayTraceBlocks(cont);
    }

    /**
     * Sets the players position and sends the to the server
     *
     * @param x the x position
     * @param y the y position
     * @param z the z position
     * */
    private void setPosition(double x,double y,double z){

        //Gets the riding entity
        Entity ridden = this.mc.player.getRidingEntity();

        //Checks if the entity is not set
        if(ridden==null){
            //Sends the position
            this.mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x,y,z,true));
            //Updates the client position
            this.mc.player.setPosition(x,y,z);
        }else{
            //Sends the entity position
            ridden.setPosition(x,y,z);
            ridden.updateRidden();
        }

    }
	
}

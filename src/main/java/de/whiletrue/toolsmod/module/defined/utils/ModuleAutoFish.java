package de.whiletrue.toolsmod.module.defined.utils;

import de.whiletrue.toolsmod.module.ModuleCategory;
import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.util.Timer;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnParticlePacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraftforge.event.TickEvent.ClientTickEvent;

public class ModuleAutoFish extends Module {

	/**
	 * Current state of the module 0 = waiting 1 = fish 2 = throw back
	 */
	private int state;
	// Timer for the fishing
	private Timer timer = new Timer();

	public ModuleAutoFish() {
		super("AutoFish", ModuleCategory.UTILS, true);
	}

	@Override
	public void onTick(ClientTickEvent evt) {
		// Gets the current holding item
		Item hand = this.mc.player.getHeldItemMainhand().getItem();

		// Checks if the rod should be thrown back
		if (this.state == 1) {
			// Updates the state
			this.state = 2;
			// Resets the timer
			this.timer.reset();
			// Checks if the current item is a fishing rod
			if (hand.equals(Items.FISHING_ROD))
				// Gets the fishing rod back in
				this.rightClick();
			return;
		}

		// Checks if the rod should be thrown back
		if (this.state == 2) {
			// Checks if the timer is done
			if (!this.timer.hasReached(500))
				return;
			// Updates the state
			this.state = 0;
			// Checks if the player is holding a fishing rod
			if (hand.equals(Items.FISHING_ROD))
				// Throws the fishing rod back
				this.rightClick();
		}
	}

	@Override
	public boolean onServerPacket(IPacket<IClientPlayNetHandler> packet) {
		// Checks if the packet is a particle packet
		if (packet instanceof SSpawnParticlePacket) {
			// Gets the packet
			SSpawnParticlePacket p = (SSpawnParticlePacket) packet;
			// Checks if the player is currently fishing
			if (this.mc.player.fishingBobber == null)
				return true;
			// Checks if the particle packet is a fishing packet
			if (!p.getParticle().getType().equals(ParticleTypes.FISHING))
				return true;
			// Checks if the fish is near enough to the fishing-bobber
			if (p.getParticleCount() < 1)
				return true;
			// Sets the state to fish now
			this.state = 1;
		}
		return true;
	}
	
	/**
	 * Rightclick's with the current item
	 */
	private void rightClick() {
		//Handles the click action
		this.mc.playerController.processRightClick(this.mc.player, this.mc.world, Hand.MAIN_HAND);
		//Swings the hand
		mc.player.swingArm(Hand.MAIN_HAND);
	}

}

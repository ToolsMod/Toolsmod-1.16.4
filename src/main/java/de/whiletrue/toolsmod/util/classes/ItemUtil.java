package de.whiletrue.toolsmod.util.classes;

import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.network.play.client.CCreativeInventoryActionPacket;
import net.minecraft.util.registry.Registry;

public class ItemUtil {

	private static ItemUtil instance;
	
	private ItemUtil() {}
	
	public static ItemUtil getInstance() {
		if(instance==null)
			instance=new ItemUtil();
		return instance;
	}
	
	/**
	 * Converts a string of json to a CompoundNBT
	 *
	 * @param nbt the nbt to convert
	 */
	public Optional<CompoundNBT> stringToNbt(String nbt) {
		try {
			return Optional.of(JsonToNBT.getTagFromJson(nbt));
		} catch (Exception e) {
			return Optional.empty();
		}
	}
	
	/**
	 * Sets an item to the players main-hand if he is in creative mode
	 * @param item the item to set
	 */
	public boolean setItem(ItemStack item) {
		Minecraft mc = Minecraft.getInstance();
		if(mc.playerController.isNotCreative())
			return false;
		//Gets the current selected slot
		int slot = mc.player.inventory.currentItem+36;

		//Sets the item
		mc.player.inventory.setInventorySlotContents(mc.player.inventory.currentItem,item);

		//Creates the packet
		CCreativeInventoryActionPacket packet = new CCreativeInventoryActionPacket(slot, item);
		//Sends the packet to the server
		mc.player.connection.sendPacket(packet);
		return true;
	}
	
	/**
	 * Returns an optional item from the name of the raw
	 *
	 * @param raw the raw name
	 */
	public Optional<Item> getItemFromName(String raw){
		if(!raw.contains(":"))
			return this.getItemFromName("minecraft", raw);
		else {
			String reg = raw.substring(0,raw.indexOf(":"));
			String itm = raw.substring(raw.indexOf(":")+1);
			return this.getItemFromName(reg,itm);
		}
	}

	/**
	 * Returns an optional item the is converted from the given registry and the name
	 *
	 * @param registry the registry name
	 * @param name the item name
	 */
	public Optional<Item> getItemFromName(String registry,String name){
		return Registry.ITEM.stream().filter(i->i.getTranslationKey().endsWith(registry+"."+name.toLowerCase())).findFirst();
	}

	/**
	 * Returns an optional block from its name
	 *
	 * @param raw the raw block name
	 */
	public Optional<Block> getBlockFromName(String raw){
		if(!raw.contains(":"))
			return this.getBlockFromName("minecraft", raw);
		else {
			String reg = raw.substring(0,raw.indexOf(":"));
			String itm = raw.substring(raw.indexOf(":")+1);
			return this.getBlockFromName(reg,itm);
		}
	}
	
	/**
	 * Returns an optional block the is converted from the given registery and the name
	 *
	 * @param registry the registry name
	 * @param name the block name
	 */
	public Optional<Block> getBlockFromName(String registry,String name){
		return Registry.BLOCK.stream().filter(i->i.getTranslationKey().endsWith(registry+'.'+name)).findFirst();
	}

	/**
	 * @param entity the armor-stand
	 * @return the NBT-tag from the armor-stand
	 */
	public CompoundNBT getTagFromArmorstand(ArmorStandEntity entity) {
		//Gets the nbt
        CompoundNBT nbt = new CompoundNBT();
        entity.writeUnlessPassenger(nbt);

        //Invalid tags
        String[] toRem={"Brain","UUIDMost","UUIDLeast","Air","Dimension","HurtByTimestamp",
        		"Attributes","FallFlying","PortalCooldown","AbsorptionAmount","FallDistance",
        		"DeathTime","Motion","Health","OnGround","HurtTime"};

        //Removes all invalid tags
        for(String s:toRem)
            nbt.remove(s);

        return nbt;
	}
}

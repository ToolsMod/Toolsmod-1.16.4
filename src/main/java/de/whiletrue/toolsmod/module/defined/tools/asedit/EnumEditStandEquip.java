package de.whiletrue.toolsmod.module.defined.tools.asedit;

import javax.annotation.Nullable;

import de.whiletrue.toolsmod.util.classes.TextUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public enum EnumEditStandEquip {

	REMOVE("remove",Items.BARRIER,null,-10,-76),
	MAINHAND("mainhand",Items.IRON_SWORD,EquipmentSlotType.MAINHAND,-21,-54),
	OFFHAND("offhand",Items.TORCH,EquipmentSlotType.OFFHAND,+1,-54),
	HEAD("head",Items.CHAINMAIL_HELMET,EquipmentSlotType.HEAD,-10,-32),
	CHEST("chest",Items.CHAINMAIL_CHESTPLATE,EquipmentSlotType.CHEST,-10,-10),
	LEGS("legs",Items.CHAINMAIL_LEGGINGS,EquipmentSlotType.LEGS,-10,12),
	FEET("feet",Items.CHAINMAIL_BOOTS,EquipmentSlotType.FEET,-10,34);
	
	//Name and copy-name
	private String name;
	//Icon of the equip
	private Item icon;
	
	//Corresponding slot
	@Nullable
	private EquipmentSlotType response;
	
	//Offset on the default model
	private int offsetX,offsetY;
	
	private EnumEditStandEquip(String key,Item icon,@Nullable EquipmentSlotType response,int offsetX,int offsetY) {
		this.response=response;
		this.icon=icon;
		this.offsetX=offsetX;
		this.offsetY=offsetY;
		this.name=key;
	}
	
	public String getName() {
		return TextUtil.getInstance().getByKey("modules.asedit.gui.settings.equip."+this.name);
	}
	public String getCopy() {
		return TextUtil.getInstance().getByKey("modules.asedit.gui.settings.equip.copy."+this.name);
	}
	
	public Item getIcon() {
		return this.icon;
	}
	
	public EquipmentSlotType getResponse() {
		return this.response;
	}
	
	public int getOffsetX() {
		return this.offsetX;
	}
	public int getOffsetY() {
		return this.offsetY;
	}
	
	public ItemStack getFromSlot(PlayerEntity entity) {
		//Checks if its the special case
		if(this.response==null)
			return ItemStack.EMPTY;
		
		//Return the slot stack
		return entity.getItemStackFromSlot(this.response);
	}
}
package de.whiletrue.toolsmod.module.defined.tools.asedit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.util.classes.ItemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.network.play.client.CUseEntityPacket.Action;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;

public class AsEditManager {

	//Reference to the game
	private Minecraft game = Minecraft.getInstance();
	
	//All saved stands
	private List<EditableArmorStand> stands = new ArrayList<EditableArmorStand>();

	//Holds the selected armor stand. If multiple are selected, it's -1
	private int selectedStand = -1;
	
	//Rotation, speed and dimension settings
	private EnumMoveType moveMode = EnumMoveType.POSITION;
	private float moveSpeed=.1f;
	private int moveAxis=0;
	
	/**
	 * Event handler for the player scroll mouse event
	 * 
	 * @param direction the direct the player scrolled
	 * @return if the event should be cancelled
	 */
	public boolean handleMouseScroll(float direction) {
		//Checks if any mode is selected
    	if(this.moveMode==null)
    		return false;
    	
    	//Calculates the scroll value
        float value = this.moveSpeed*direction;
        
        //Checks if multiple stands are selected
        if(this.selectedStand==-1)
        	this.scrollMultiple(value);
        else
        	this.scrollSingle(value);
        
        return true;
	}
	
	/**
	 * Used by {@link #handleMouseScroll(float)}
	 * Handles the scrolling of multiple stands
	 * 
	 * @param value the scroll value
	 */
	private void scrollMultiple(float value) {
		//Checks the mode
    	if(EnumMoveType.POSITION.equals(this.moveMode)) {
    		//Updates the position
    		for(EditableArmorStand as : this.stands)
    			//If selected
    			if(as.isSelected())
        			//Update position
            		as.updatePosition(this.moveAxis,value/10);
    		return;
    	}
    	
		//Gets the middle position
		EditableArmorStand middle = this.stands.stream().filter(i->i.isSelected()).findFirst().get();
		
		//Gets the angle
		float angle = value/10;
		
		//Updates the stands
		for(EditableArmorStand as : this.stands)
			//If selected
			if(as.isSelected())
				//Move
				as.moveInRelationToStand(middle, angle);
	}

	/**
	 * Used by {@link #handleMouseScroll(float)}
	 * Handles the scrolling of a single armor stand
	 * 
	 * @param value the scroll value
	 */
	private void scrollSingle(float value) {
		//Gets the selected stand
    	EditableArmorStand as = this.stands.get(this.selectedStand);
    	
    	//Applies the event
    	switch (this.moveMode){
    	case ROTATION:
    		as.updateGeneralRotation(value);
    		break;
    	case POSITION:
    		as.updatePosition(this.moveAxis,value/10);
    		break;
    	default:
    		//Updates the entity's rotation
    		as.updateBodyRotation(this.moveMode,this.moveAxis,value);
    		break;
    	}
	}

	
	/**
	 * Event handler for the interaction with the stand
	 * 
	 * @param packet the packet that the client send
	 * @return if the packet should be cancelled
	 */
	public boolean handleUseStand(CUseEntityPacket packet) {
		//Gets the entity and the action
    	Entity ent = packet.getEntityFromWorld(this.game.world);
    	Action act = packet.getAction();
    	
    	//Checks if the packet addresses and edit stand
    	if(!(ent instanceof EditableArmorStand))
    		return false;
    	
    	//Gets the stand
    	EditableArmorStand stand = (EditableArmorStand) ent;
    	
    	//Checks if the slots are editable
    	if(stand.areSlotsDisabled())
    		return true;
    	
    	//Checks if the stands main item should be removed
    	if (act.equals(CUseEntityPacket.Action.ATTACK)) {
            //Removes the holding item
            stand.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.AIR));
            return true;
        }

        //Gets the players held item
        ItemStack held = this.game.player.getHeldItem(Hand.MAIN_HAND).copy();
    	
        //Gets the height of the click
        double y = packet.getHitVec().getY();

        //Gets the clicked slot
        EquipmentSlotType slot =
        	y > 1.5 ? EquipmentSlotType.HEAD :
        	(y > .7 ? EquipmentSlotType.CHEST :
        	(y > .5 ? EquipmentSlotType.LEGS :
        	EquipmentSlotType.FEET));

        //Checks if the player holds nothing
        if(held.getItem().equals(Items.AIR)){
            //Removes the stand slot
        	stand.setItemStackToSlot(slot,new ItemStack(Items.AIR));
            return true;
        }
        
        //Checks if the player holds an armor piece
        if (held.getItem() instanceof ArmorItem) {
            //Gets the armor-item
            ArmorItem ai = (ArmorItem) held.getItem();

            //Updates the armor-stands equipment-slot
            stand.setItemStackToSlot(ai.getEquipmentSlot(), held);
            return true;
        }
        
        //Sets the item to the main-hand
        stand.setItemStackToSlot(EquipmentSlotType.MAINHAND, held);
        return true;
	}
	
	/**
	 * Event handler when the user selects one or multiple stands
	 * @param id
	 */
	public void handleSelect(int id) {
		//Sets the new selected stand
		this.setSelected(Math.min(id, this.stands.size()-1));
		
		//Ensures that at least one stand exits
		if(this.stands.isEmpty()) {
			this.addDefaultStand();
		//Checks if multiple stands got selected
		}else if(id == -1) {
			//Checks if any invalid mode is chosen
    		if(this.moveMode != null && !this.moveMode.canHandleMultiple())
				//Unselect's the mode
    			this.setMode(this.moveMode);
		}else {
			//Selects only that stand
    		for(int i=0;i<this.stands.size();i++)
    			this.stands.get(i).setSelected(i==this.selectedStand);
		}
	}
	
	/**
	 * Removes all stands from the world without readding the default stand
	 */
	public void deleteAllStandsNoCheck() {
		//Removes all stands from the world
    	this.stands.forEach(i->this.game.world.removeEntityFromWorld(i.getEntityId()));
    	//Clears all stands
    	this.stands.clear();
	}
	
	/**
	 * Deletes all stands and reads the default stand
	 */
	public void deleteAllStands() {
		//Deletes all stands
		this.deleteAllStandsNoCheck();
		//Adds the default stand
		this.addDefaultStand();
	}
	
	/**
	 * Teleport's all selected stands to the player
	 */
	public void doTeleportToPlayer() {
		//Gets all selected stands
    	EditableArmorStand[] selected = this.stands.stream().filter(i->i.isSelected()).toArray(EditableArmorStand[]::new);
    	
    	//Gets the middle position
    	Vector3d middle = selected[0].getPositionVec();
    	
    	//Gets the player position
    	Vector3d pos = this.game.player.getPositionVec();
    	
    	//Teleport's all stands in relation to the stand
    	for(EditableArmorStand s : selected) {
    		//Calculates the position
    		Vector3d sdiff = s.getPositionVec().subtract(middle);
    		
    		Vector3d diff = pos.add(sdiff);
    		
    		//Teleport's the stand to the player
    		s.setPosition(diff.getX(),diff.getY(),diff.getZ());
    	}
	}

	/**
	 * Moves the stand at the index up or down at the list
	 * @param index the index of the stand
	 * @param up if the stand should be moved up or down
	 */
	public void doMoveList(int index, boolean up) {
		//Gets the other index
    	int swapIndex = index+(up?1:-1);
    	
    	//Gets the stand
    	EditableArmorStand as = this.stands.get(index);
    	
    	//Gets the other
    	EditableArmorStand swap = this.stands.get(swapIndex);
    	
    	//Swaps them
    	this.stands.set(index, swap);
    	this.stands.set(swapIndex, as);
	}
	
	/**
	 * Clones all selected stands
	 * 
	 * @return the index of the first cloned stand
	 */
	public int doClone() {
		//Clones all selected stands
    	EditableArmorStand[] cloned = this.stands.stream()
    			.filter(i->i.isSelected())
    			.map(i->{
    				//Unselect's the previous
    				i.setSelected(false);
    				//Clones it
    				EditableArmorStand clone = i.doClone();
    				//Selects the clone
    				clone.setSelected(true);
    				return clone;
    			})
    			.toArray(EditableArmorStand[]::new);

    	//Adds them
    	this.addAll(cloned);
    	
    	//Gets the cloned index
    	int index = this.stands.indexOf(cloned[cloned.length-1]);
    	
    	//Updates the selection
    	this.handleSelect(cloned.length<=1?index:-1);
    	
    	return index;
	}
	
	/**
	 * Deletes all selected stand
	 * 
	 * @return the index of the new selected stand
	 */
	public int doDelete() {
		//Removes the selected
    	new ArrayList<>(this.stands).forEach(i->{
    		//Checks if the stand is selected
    		if(!i.isSelected())
    			return;
    		//Removes them from the world
    		this.game.world.removeEntityFromWorld(i.getEntityId());
    		
    		//Removes them from the list
    		this.stands.remove(i);
    	});
    	
    	//Handles the selection update
    	for(int i=0;i<this.stands.size();i++)
    		if(this.stands.get(i).isSelected()) {
    			this.handleSelect(i);
    			return i;
    		}
    	
    	//Gets the selected stand's index
    	int index = this.selectedStand==-1?this.stands.size()-1:this.selectedStand;
    	
    	//If no selected stand got found, select first one
    	this.handleSelect(index);
    	
    	return index;
	}
	
	/**
	 * Creates a new stand
	 * 
	 * @return the index of the new stand
	 */
	public int doCreateNew() {
		//Unselect's all previous
    	this.stands.forEach(i->i.setSelected(false));
    	
    	//Creates the new stand
    	this.addNewStand(this.game.player.getPosition(), null);
    	
    	//Updates the selection
    	return this.setSelected(this.stands.size()-1);
	}
	
	/**
     * Sets an item to a slot if only one entity is selected
     * @param item the item to set
     * @param slot the slot that is used
     */
    public void doItemToSelected(ItemStack item,EquipmentSlotType slot) {
    	//Gets the selected
    	EditableArmorStand as = this.stands.get(this.selectedStand);
    	
    	//Sets the stack
    	as.setItemStackToSlot(slot, item.copy());
    }
	
	/**
     * Adds the given stands
     * @param stands the stands
     */
    public void addAll(EditableArmorStand... stands) {
    	//Adds all stands
    	for(int i=0;i<stands.length;i++) {
    		//Gets the stands
    		EditableArmorStand s = stands[i];
    		//Adds him
    		this.stands.add(s);
    		this.game.world.addEntity(s.getEntityId(), s);
    	}
    	
    	//Updates the selection
    	this.setSelected(Math.min(this.stands.size()-1, this.selectedStand));
    }
    
    /**
	 * Adds the default stand if he doesn't exists
	 */
	private void addDefaultStand() {
		//Checks if any stands exist
		if(!this.stands.isEmpty())
			return;
		
		//Creates the default stand
		EditableArmorStand stand = this.addNewStand(this.game.player.getPosition(), null);
		//Selects it
		stand.setSelected(true);
		
		//Adds him and sets the selected stand
		this.setSelected(0);
	}
	
	/**
     * Adds a new stand
     * @param position the position where to spawn the stand
     * @param tag optionally the tag of the stand
     */
    public EditableArmorStand addNewStand(BlockPos position,@Nullable CompoundNBT tag) {
    	//Creates the entity
        EditableArmorStand stand= new EditableArmorStand(position.getX(),position.getY(),position.getZ());
        //Appends the nbt-tag
        if(tag!=null)
        	stand.read(tag);
        //Adds to the list
        this.stands.add(stand);
        //Adds the entity to the world
        this.game.world.addEntity(stand.getEntityId(),stand);
        
        return stand;
    }
    
    
    
    
    /**
     * @return the rotations (or position) of the selected mode
     */
    public Vector3d getGlobalRotations() {
    	//Checks if the position is selected
    	if(EnumMoveType.POSITION.equals(this.moveMode))
    		//Gets the position
    		return this.getSelectedStand().getPositionVec();
    	
    	//Gets the default rotations
		Rotations rot = this.moveMode.getRotation(this.getSelectedStand());
		return new Vector3d(rot.getX(), rot.getY(), rot.getZ());
    }
    
    /**
     * Sets the rotations for the current stand and the current mode
     * @param axis the axis to set on
     * @param rotation the rotation to set
     */
    public void setGlobalRotation(int axis,float rotation) {
    	//Checks if the mode is positioning
    	if(this.moveMode.equals(EnumMoveType.POSITION)) {
    		this.getSelectedStand().setPosition(axis, rotation);
    		return;
    	}

    	//Sets the new rotation
    	this.getSelectedStand().setBodyRotation(this.moveMode, axis, rotation);
    }
    
    /**
     * @return all stands converted as itemstack's
     */
    public List<ItemStack> getConvertedStands(){
    	//Counter
    	AtomicInteger counter = new AtomicInteger(1);
    	
    	//Gets all stands
    	return this.stands.stream()
    	//Converts them to their item
    	.map(i->{
    		//Gets the tag
    		CompoundNBT nbt = ItemUtil.getInstance().getTagFromArmorstand(i);

            CompoundNBT entitytag = new CompoundNBT();
            entitytag.put("EntityTag",nbt);

            //Create the item
            ItemStack stack = new ItemStack(Items.ARMOR_STAND);
            stack.setTag(entitytag);
            stack.setDisplayName(new StringTextComponent(String.format(
            		"§8[§%cASEdit§8] §%c%d/%d",
            		Toolsmod.COLOR_MAIN,
            		Toolsmod.COLOR_SECONDARY,
            		counter.getAndIncrement(),
            		this.stands.size())
            ));
            
            return stack;
    	})
    	//Gets them
    	.collect(Collectors.toList());
    }
    
    /**
     * Sets the selected stand and returns the id
     * @param id the selected stand
     * @return the given id
     */
    private int setSelected(int id) {
    	this.selectedStand=id;

		//Updates the info display
		Toolsmod.getInstance().getModuleManager().updateInfoDisplay();
		
		return id;
    }
    
	public EnumMoveType getMode() {
		return this.moveMode;
	}
	public void setMode(EnumMoveType mode) {
		this.moveMode = mode;

		//Updates the info display
		Toolsmod.getInstance().getModuleManager().updateInfoDisplay();
	}
	public float getSpeed() {
		return this.moveSpeed;
	}
	public void setSpeed(float speed) {
		this.moveSpeed = speed;
		
		//Updates the info display
		Toolsmod.getInstance().getModuleManager().updateInfoDisplay();
	}
	public int getAxis() {
		return this.moveAxis;
	}
	public void setAxis(int axis) {
		this.moveAxis = axis;

		//Updates the info display
		Toolsmod.getInstance().getModuleManager().updateInfoDisplay();
	}
	public List<EditableArmorStand> getStands() {
		return this.stands;
	}
	
	@Nullable
	public EditableArmorStand getSelectedStand() {
		return this.selectedStand==-1||this.stands.size()<=0?null:this.stands.get(this.selectedStand);
	}
}














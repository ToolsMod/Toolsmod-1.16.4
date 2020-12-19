package de.whiletrue.toolsmod.module.defined.tools.asedit;

import de.whiletrue.toolsmod.util.Timer;
import de.whiletrue.toolsmod.util.classes.ItemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.math.vector.Vector3d;

public class EditableArmorStand extends ArmorStandEntity{

	//Entity id counter (Other spectrum)
	private static int COUNTER_ID = 0xFFFFFF;
	
	//Timer for the select of an entity (Visual)
	private Timer selectTimer;
	
	//Group or reference name to determine armor stands
	private String referenceName;
	
	//If the armor stand is selected
	private boolean selected = false;
	
	public EditableArmorStand(ArmorStandEntity stand) {
		super(EntityType.ARMOR_STAND,Minecraft.getInstance().world);
		this.copyDataFromOld(stand);
		this.referenceName="";
	}
	
	public EditableArmorStand(double x,double y,double z) {
		super(EntityType.ARMOR_STAND, Minecraft.getInstance().world);
		//Sets the id
		this.setEntityId(COUNTER_ID++);
		//Sets the position
		this.setPosition(x, y, z);
		
		//Sets the reference name
		this.referenceName = "New armorstand";
	}
	
	@Override
	public boolean isGlowing() {
		//Checks if the timer is ongoing
    	if(this.selectTimer!= null) {
    		//Checks if the timer is finished
    		if(this.selectTimer.hasReached(1000))
    			//Resets the timer
    			this.selectTimer=null;
    		else
    			return true;
    	}
    	
    	return super.isGlowing();
	}

	/**
	 * @return If the entity is glowing natural
	 */
	public boolean isGlowingNatrual() {
		return super.isGlowing();
	}
	
	/**
	 * Resets the whole stand back to the default
	 */
	public void doReset() {
		//Gets the position
        Vector3d position = this.getPositionVec();

        //Resets basics property's
        this.read(new CompoundNBT());
        //Resets name
        this.setCustomName(null);
        //Resets armor
        for(EquipmentSlotType slot:EquipmentSlotType.values())
        	this.setItemStackToSlot(slot, ItemStack.EMPTY);
        //Resets position
        this.setPosition(position.getX(),position.getY(),position.getZ());
	}
	
	/**
	 * Clones the current armor stand
	 * @return
	 */
	public EditableArmorStand doClone() {
		//Clones the NBT
    	CompoundNBT clonedNbt = ItemUtil.getInstance().getTagFromArmorstand(this);

    	//Creates the stand
    	EditableArmorStand clone = new EditableArmorStand(this.getPosX(), this.getPosY(), this.getPosZ());

    	//Appends the nbt
    	if(clonedNbt != null)
    		clone.read(clonedNbt);
    	
    	//Appends the reference name
    	clone.referenceName=this.referenceName;
    	
    	return clone;
	}
	
	/**
	 * Gets the value of an attribute from the armor stand
	 * @param attrId the attributes id
	 * @return the attributes value from the entity
	 */
	public boolean getAttr(int attrId) {
		return (this.dataManager.get(STATUS) & attrId) != 0;
	}
	
	/**
	 * Updates the attribute with the attrId to the state
	 * 
	 * @param attrId the attributes id
	 * @param state the new state
	 */
	public void setAttr(int attrId,boolean state) {
		//Gets the parameter
        byte dataParameter = this.dataManager.get(STATUS);
        //Sets the state
        this.dataManager.set(STATUS, (byte)(state?(dataParameter|attrId):(dataParameter&~attrId)));
	}
	
	/**
	 * Swaps the value of the attribute
	 * @param attrId the id of the attribute
	 */
	public void swapAttr(int attrId) {
		this.setAttr(attrId, this.getAttr(attrId));
	}
	
	
	
	/**
     * Updates the axis on the given type
     *
     * @param type the type
     * @param dimension the dimension that should be edited (X=0,Y=1,Z=2)
     * @param value the value that should be added
     */
    public void updateBodyRotation(EnumMoveType type, int dimension, float value){
        //Gets the old rotation
        Rotations old = type.getRotation(this);

        //Gets the values
        float x = old.getX()+(dimension==0?value:0);
        float y = old.getY()+(dimension==1?value:0);
        float z = old.getZ()+(dimension==2?value:0);

        //Sets the new rotation
        type.setRotation(this,new Rotations(x%360,y%360,z%360));
    }
    
    /**
     * Sets the axis on the given type to the value
     *
     * @param type the type
     * @param dimension the dimension that should be edited (X=0,Y=1,Z=2)
     * @param value the value that should be added
     */
    public void setBodyRotation(EnumMoveType type, int dimension, float value){
        //Gets the old rotation
        Rotations old = type.getRotation(this);

        //Gets the values
        float x = (dimension==0?value:old.getX());
        float y = (dimension==1?value:old.getY());
        float z = (dimension==2?value:old.getZ());

        //Sets the new rotation
        type.setRotation(this,new Rotations(x%360,y%360,z%360));
    }

    /**
     * Updates the entity's rotation
     *
     * @param value the value to update by
     */
    public void updateGeneralRotation(float value){
        this.rotationYaw=(this.rotationYaw+value)%360;
    }
    
    /**
     * Rotates the stand in relation to the other stand by the given angle
     * @param stand the stand that's in the middle
     * @param angle the angle
     */
    public void moveInRelationToStand(EditableArmorStand stand,float angle) {
    	//Updates the rotation
		this.updateGeneralRotation((float) (angle/(Math.PI*2f)*360f));
    	
		//If the same stand
		if(this.equals(stand))
			return;
		
		/*
		 * Rotation code got from: http://www.java-gaming.org/index.php?topic=31033.0
		 * */        			
		double x = this.getPosX() - stand.getPosX();
		double y = this.getPosZ() - stand.getPosZ();
	    double newx = x * Math.cos(angle) - y * Math.sin(angle);
	    double newy = x * Math.sin(angle) + y * Math.cos(angle);
	    
	    //Sets the stands new position
	    this.setPosition(newx+stand.getPosX(), this.getPosY(), newy+stand.getPosZ());
    }

    /**
     * Updates the entity's position
     *
     * @param dimension
     * @param value
     */
    public void updatePosition(int dimension,float value){

        //Gets the old coordinates
        double x = this.getPosX()+(dimension==0?value:0);
        double y = this.getPosY()+(dimension==1?value:0);
        double z = this.getPosZ()+(dimension==2?value:0);

        //Sets the new position
        this.setPosition(x,y,z);
    }

    /**
     * Sets the position on the given axis
     * @param dimension the axis
     * @param value the value
     */
    public void setPosition(int dimension,float value) {
    	//Gets the old coordinates
        double x = dimension==0?value:this.getPosX();
        double y = dimension==1?value:this.getPosY();
        double z = dimension==2?value:this.getPosZ();
        
        //Sets the new position
        this.setPosition(x,y,z);
    }
    
    public void setDisabledSlots(boolean disabled) {
    	this.disabledSlots=disabled?0x1fffff:0;
    }
    
    public boolean areSlotsDisabled() {
    	return this.disabledSlots!=0;
    }
    
    public boolean isSelected() {
		return this.selected;
	}
    public void setSelected(boolean selected) {
		//Checks if the selection is an update
		if(this.selected==selected)
			return;

		//Sets the new state
		this.selected = selected;
		
		//If new selected
		if(selected)
			//Restarts the timer
			this.selectTimer=new Timer();
		else
			//Stops the glowing timer
			this.selectTimer=null;
	}
    
    public String getReferenceName() {
		return this.referenceName;
	}
    public void setReferenceName(String referenceName) {
		this.referenceName = referenceName;
	}
}

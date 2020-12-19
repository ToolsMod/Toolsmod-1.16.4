package de.whiletrue.toolsmod.module.defined.tools.asedit;

import de.whiletrue.toolsmod.util.classes.TextUtil;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.util.math.Rotations;

public enum EnumMoveType {
    HEAD("head", ArmorStandEntity::getHeadRotation,(r, e)->e.setHeadRotation(r)),
    BODY("body", ArmorStandEntity::getBodyRotation,(r, e)->e.setBodyRotation(r)),
    LEFT_LEFT("lleg", ArmorStandEntity::getLeftLegRotation,(r, e)->e.setLeftLegRotation(r)),
    RIGHT_LEG("rleg", ArmorStandEntity::getRightLegRotation,(r, e)->e.setRightLegRotation(r)),
    LEFT_ARM("larm", ArmorStandEntity::getLeftArmRotation,(r, e)->e.setLeftArmRotation(r)),
    RIGHT_ARM("rarm", ArmorStandEntity::getRightArmRotation,(r, e)->e.setRightArmRotation(r)),
    POSITION("pos"),
    ROTATION("rotation");

    //Executor to get the right rotation
    private IGetRotation getRotation;
    //Executor to set the right rotation
    private ISetRotation setRotation;

    private String key;
    
    private EnumMoveType(String key){
    	this.key=key;
    }

    private EnumMoveType(String key, IGetRotation getRotation, ISetRotation setRotation) {
        this.getRotation=getRotation;
        this.setRotation=setRotation;
        this.key=key;
    }

    /**
     * @return if the current mode can handle multiple stands
     */
    public boolean canHandleMultiple() {
    	return this.equals(POSITION) || this.equals(ROTATION);
    }
    
    public String getName() {
        return TextUtil.getInstance().getByKey("modules.asedit.gui.adjust.mode."+this.key);
    }

    public String getShortName() {
        return TextUtil.getInstance().getByKey("modules.asedit.gui.adjust.mode.short."+this.key);
    }

    public void setRotation(EditableArmorStand entity, Rotations rotation){
        this.setRotation.execute(rotation, entity);
    }

    public Rotations getRotation(EditableArmorStand entity) {
        return getRotation.execute(entity);
    }

    @FunctionalInterface
    public interface ISetRotation{
        void execute(Rotations rotations,EditableArmorStand entity);
    }

    @FunctionalInterface
    public interface IGetRotation{
        Rotations execute(EditableArmorStand entity);
    }
}

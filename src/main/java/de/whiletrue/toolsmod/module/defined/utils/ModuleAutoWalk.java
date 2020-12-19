package de.whiletrue.toolsmod.module.defined.utils;

import de.whiletrue.toolsmod.module.ModuleCategory;
import de.whiletrue.toolsmod.module.defined.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent.ClientTickEvent;

public class ModuleAutoWalk extends Module{

    //Movement key
    private KeyBinding key = this.mc.gameSettings.keyBindForward;
	
	public ModuleAutoWalk() {
		super("AutoWalk", ModuleCategory.UTILS, true);
	}
	
	@Override
	public void onEnable() {
		//Sets the key listener to all
    	this.key.setKeyConflictContext(KeyConflictContext.UNIVERSAL);
	}

	@Override
    public void onDisable() {
		//Resets the key
    	this.key.setKeyConflictContext(KeyConflictContext.IN_GAME);
    	
        //Gets the window-id
        long id = Minecraft.getInstance().getMainWindow().getHandle();

        //Updates the key
        KeyBinding.setKeyBindState(this.key.getDefault(), InputMappings.isKeyDown(id,this.key.getDefault().getKeyCode()));
    }

	@Override
	public void onTick(ClientTickEvent event) {
		//Updates the key
		this.key.setPressed(true);
	}
}

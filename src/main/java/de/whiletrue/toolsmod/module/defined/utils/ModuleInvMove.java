package de.whiletrue.toolsmod.module.defined.utils;

import de.whiletrue.toolsmod.module.ModuleCategory;
import de.whiletrue.toolsmod.module.defined.Module;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent.ClientTickEvent;

public class ModuleInvMove extends Module{

	//Keybinding
    private KeyBinding[] keys = {
            this.mc.gameSettings.keyBindForward,
            this.mc.gameSettings.keyBindBack,
            this.mc.gameSettings.keyBindLeft,
            this.mc.gameSettings.keyBindRight,
            this.mc.gameSettings.keyBindJump
    };
    
	public ModuleInvMove() {
		super("InvMove", ModuleCategory.UTILS, true);
	}
	
	@Override
	public void onEnable() {
        for(KeyBinding bind:this.keys)
        	bind.setKeyConflictContext(KeyConflictContext.UNIVERSAL);
	}
	
	@Override
	public void onDisable() {
        for(KeyBinding bind:this.keys)
        	bind.setKeyConflictContext(KeyConflictContext.IN_GAME);
	}
	
	@Override
	public void onTick(ClientTickEvent event) {
		//Checks if a GUI is open and its not the chat
        if(this.mc.currentScreen==null || this.mc.currentScreen instanceof ChatScreen)
            return;
        
        //Gets the window-id
        long id = this.mc.getMainWindow().getHandle();
		
        //Iterates over all move-keys
        for(KeyBinding bind : this.keys)
        	//Updates the keys
        	bind.setPressed(InputMappings.isKeyDown(id,bind.getDefault().getKeyCode()));
	}
}

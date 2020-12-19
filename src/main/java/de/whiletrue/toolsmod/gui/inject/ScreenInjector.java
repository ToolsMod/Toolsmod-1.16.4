package de.whiletrue.toolsmod.gui.inject;

import net.minecraftforge.client.event.GuiScreenEvent;

public abstract class ScreenInjector{

	/**
	 * Executes before the screen inits
	 * @param screen the screen
	 */
	public abstract void preInit(GuiScreenEvent.InitGuiEvent.Pre evt);
	
	/**
	 * Executes after the screen inits
	 * @param screen
	 */
	public abstract void postInit(GuiScreenEvent.InitGuiEvent.Post evt);
}

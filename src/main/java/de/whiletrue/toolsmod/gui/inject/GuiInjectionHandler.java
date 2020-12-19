package de.whiletrue.toolsmod.gui.inject;

import java.util.HashMap;
import java.util.Map;

import de.whiletrue.toolsmod.gui.inject.screen.*;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.client.event.GuiScreenEvent;

public class GuiInjectionHandler {

	//Registered gui overloads
	private static final Map<Class<? extends Screen>, ScreenInjector> INJECTED_GUIS = new HashMap<Class<? extends Screen>, ScreenInjector>(){
		private static final long serialVersionUID = 1L;
		{
			//Registers all injectors
			this.put(MainMenuScreen.class, new MainMenuInjector());
		}
	};
	
	/**
	 * Executes before a screen init's
	 * @param screen the screen
	 */
	public static void handlePreInit(GuiScreenEvent.InitGuiEvent.Pre evt) {
		try {			
			INJECTED_GUIS.get(evt.getGui().getClass()).preInit(evt);
		} catch (Exception e) {}
	}
	
	/**
	 * Executes after a screen init's
	 * @param screen
	 */
	public static void handlePostInit(GuiScreenEvent.InitGuiEvent.Post evt) {
		try {			
			INJECTED_GUIS.get(evt.getGui().getClass()).postInit(evt);
		} catch (Exception e) {}
	}
	
}

package de.whiletrue.toolsmod.util;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.InputMappings.Input;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.settings.KeyModifier;

public class Keybind {
	
	//The key modifiers
	private KeyModifier modifiers = KeyModifier.NONE;
	
	//The keyCode and scanCode
	private int keyCode=256;
	
	/**
	 * Empty keybind
	 */
	public Keybind() {}
	
	public Keybind(int keyCode,KeyModifier modifiere) {
		this.keyCode=keyCode;
		this.modifiers=modifiere;
	}
	
	/**
	 * Updates the keycode and modifiers;
	 * @param keyCode the key
	 * @param modifier the modifire
	 */
	public void update(int keyCode,KeyModifier modifier) {
		this.keyCode=keyCode;
		this.modifiers=modifier;
	}
	
	/**
	 * Updates the keybind settings (modifire and keycode) from an key event
	 * @param keyCode the events keycode
	 */
	public void updateFromEvent(int keyCode) {
		this.modifiers=KeyModifier.getActiveModifier();
		this.keyCode=keyCode;
	}
	
	/**
	 * @param keyCode the keyCode
	 * @return if the keyCode is pressed and the modifiers match
	 */
	public boolean isKeycodeMatching(int keyCode) {
		return this.keyCode != 256 && this.keyCode==keyCode && this.modifiers.isActive(null);
	}
	
	/**
	 * Gets the formatted combined name of the bind
	 * @return the name
	 */
	public String getName() {
		//Gets the temp input
		Input in = InputMappings.getInputByCode(this.keyCode,0);
		
		// Gets the text
		//Checks if the code is esc
		return this.keyCode == 256 ? "" :
			//Checks if the code is a modifier
			KeyModifier.isKeyCodeModifier(in) ? this.modifiers.name() :
				//Gets the combined name
				(this.modifiers.getCombinedName(in, () ->{
					//Gets the mapped name
					String x = GLFW.glfwGetKeyName(this.keyCode,-1);
					//Check if the mapped name got found, otherwise format the translation key
					return new StringTextComponent(x == null ? I18n.format(in.getTranslationKey()) : x);
				})).getString();
	}
	
	public int getKeyCode() {
		return this.keyCode;
	}
	
	public KeyModifier getModifiers() {
		return this.modifiers;
	}
}

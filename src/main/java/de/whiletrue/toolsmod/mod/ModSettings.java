package de.whiletrue.toolsmod.mod;

import org.lwjgl.glfw.GLFW;

import de.whiletrue.toolsmod.settings.defined.*;
import de.whiletrue.toolsmod.util.Keybind;
import net.minecraftforge.client.settings.KeyModifier;

public class ModSettings {
	
	//Speed of which the quick access gui scales
	public static final SettingInteger quickAccessGuiSpeed = new SettingInteger()
			.min(1)
			.max(15)
			.slider("")
			.standard(3)
			.name("qaGuiSpeed");

	//Keybind of the quickaccess gui
	public static final SettingKeybind quickAccessKeybind = new SettingKeybind()
			.standard(new Keybind(GLFW.GLFW_KEY_X, KeyModifier.NONE))
			.name("qaGuiBind");

	//If the mod should check for updates on startup
	public static final SettingBool checkForRemoteVersion = new SettingBool()
			.standard(true)
			.name("checkVersion");
}

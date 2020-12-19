package de.whiletrue.toolsmod.mod;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;

import de.whiletrue.toolsmod.settings.Setting;
import de.whiletrue.toolsmod.util.classes.FileUtil;

public class SettingsManager {
	
	//Holds the settings file
	private final File settingsFile;
	
	//Hold all settings
	private List<Setting<?>> settingsList;
	
	public SettingsManager(String settingsFile) {
		this.settingsFile=new File(Toolsmod.ID + '/' + settingsFile);
		
		//Gets all fields
		this.settingsList = Arrays.stream(ModSettings.class.getDeclaredFields())
		//Checks if the type is a setting
		.filter(i->Setting.class.isAssignableFrom(i.getType()))
		//Gets the values
		.map(i->{
			try {
				i.setAccessible(true);
				return (Setting<?>)i.get(null);
			} catch (IllegalArgumentException | IllegalAccessException e) {}
			return null;
		})
		//Collects them
		.collect(Collectors.toList());
		
		//Loads all settings
		this.load();
	}
	
	/**
	 * Saves all settings
	 */
	public void save() {
		// Save object
		JsonObject save = new JsonObject();

		// For every setting
		for (Setting<?> set : this.settingsList)
			//Appends the setting
			save.addProperty(set.getName(), set.handleSave());

		// Saves the object
		FileUtil.getInstance().printToFile(this.settingsFile.getAbsolutePath(), save);
	}

	/**
	 * Loads all settings
	 */
	public void load() {
		try {
			// Gets the content
			JsonObject saved = FileUtil.getInstance().loadFileAsJson(this.settingsFile.getAbsolutePath()).get()
					.getAsJsonObject();

			//Iterates over all settings
			for(Setting<?> set : this.settingsList)
				//Checks if the setting is contained
				if(saved.has(set.getName()))
					//Parses the setting
					set.handleParse(saved.get(set.getName()).getAsString());
		} catch (Exception e) {
			e.printStackTrace();
			// If anything went wrong, save the current configuration
			this.save();
		}
	}
	
	public List<Setting<?>> getSettings() {
		return this.settingsList;
	}
}

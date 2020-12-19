package de.whiletrue.toolsmod.module;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.module.defined.tools.ModuleAsEdit;
import de.whiletrue.toolsmod.module.defined.tools.ModuleBannerWriter;
import de.whiletrue.toolsmod.module.defined.tools.ModuleGhostsight;
import de.whiletrue.toolsmod.module.defined.tools.ModuleHeadWriter;
import de.whiletrue.toolsmod.module.defined.tools.ModuleMapcreator;
import de.whiletrue.toolsmod.module.defined.tools.ModuleTeleport;
import de.whiletrue.toolsmod.module.defined.tools.ModuleTimer;
import de.whiletrue.toolsmod.module.defined.utils.ModuleAutoFish;
import de.whiletrue.toolsmod.module.defined.utils.ModuleAutoWalk;
import de.whiletrue.toolsmod.module.defined.utils.ModuleEntityFly;
import de.whiletrue.toolsmod.module.defined.utils.ModuleFastbreak;
import de.whiletrue.toolsmod.module.defined.utils.ModuleFastplace;
import de.whiletrue.toolsmod.module.defined.utils.ModuleInvMove;
import de.whiletrue.toolsmod.module.defined.utils.ModuleNoFall;
import de.whiletrue.toolsmod.module.defined.utils.ModuleSprint;
import de.whiletrue.toolsmod.module.defined.utils.ModuleStep;
import de.whiletrue.toolsmod.module.defined.utils.ModuleVelocity;
import de.whiletrue.toolsmod.module.defined.visual.ModuleFullbright;
import de.whiletrue.toolsmod.module.defined.visual.ModuleShulkerView;
import de.whiletrue.toolsmod.settings.Setting;
import de.whiletrue.toolsmod.util.classes.FileUtil;
import net.minecraft.client.Minecraft;

public class ModuleManager {

	// File for the saves
	private final File saveFile;

	// List with all modules
	private List<Module> registeredModules;
	// All enabled and disabled modules
	private List<Module> enabledModules = new ArrayList<>();
	private List<Module> disabledModules = new ArrayList<>();

	// Current saved module display info
	private String[][] moduleStats;

	public ModuleManager(String saveFile) {
		this.saveFile = new File(Toolsmod.ID+'/'+saveFile);
		
		//Registers all modules
		this.registeredModules=Arrays.asList(
			//Movement
			new ModuleAutoWalk(),
			new ModuleEntityFly(),
			new ModuleInvMove(),
			new ModuleSprint(),
			new ModuleStep(),
			new ModuleTeleport(),
			new ModuleVelocity(),
			
			//Player
			new ModuleAutoFish(),
			new ModuleGhostsight(),
			new ModuleNoFall(),
			new ModuleTimer(),
			
			//Special
			new ModuleAsEdit(),
			new ModuleBannerWriter(),
			new ModuleHeadWriter(),
			new ModuleMapcreator(),
			
			//Visual
			new ModuleFullbright(),
			new ModuleShulkerView(),
			
			//World
			new ModuleFastbreak(),
			new ModuleFastplace()
		);
		//Inits all mods
		this.registeredModules.forEach(i->i.init());
		
		// Disables all modules
		this.disabledModules.addAll(this.registeredModules);
	}

	/**
	 * Saves all modules with their settings
	 */
	public void save() {
		// Save object
		JsonObject save = new JsonObject();

		// For every module
		for (Module mod : this.getModules()) {
			// Module-save
			JsonObject modSave = new JsonObject();

			// Adds the key-bind
			modSave.addProperty("key", mod.getKeyBind());
			// Adds the state
			modSave.addProperty("enabled", mod.isActive());
			// Adds if the module is allowed
			modSave.addProperty("allowed", mod.isAllowed());

			// Settings object
			JsonObject settings = new JsonObject();

			//Iterates over all settings from the module
			for(Setting<?> set : mod.getSettings())
				//Appends the value
				settings.addProperty(set.getName(), set.handleSave());
			
			// Appends the settings
			modSave.add("settings", settings);

			// Appends the module
			save.add(mod.getName(), modSave);
		}

		// Saves the object
		FileUtil.getInstance().printToFile(this.saveFile.getAbsolutePath(), save);
	}

	/**
	 * Loads all modules with their settings
	 */
	public void load() {
		try {
			// Gets the content
			JsonObject saved = FileUtil.getInstance().loadFileAsJson(this.saveFile.getAbsolutePath()).get()
					.getAsJsonObject();

			//Iterates over all modules
			for(Module mod : this.getModules()) {
				//Checks if settings contains the mod
				if(!saved.has(mod.getName()))
					continue;
				
				// Gets the data
				JsonObject data = saved.get(mod.getName()).getAsJsonObject();
				
				// Sets if the module is allowed
				mod.setAllowed(data.get("allowed").getAsBoolean());
				
				// Checks if the module can be enabled at start and should be enabled
				if (mod.isAllowed() && mod.isAvaiableAtStart() && data.get("enabled").getAsBoolean())
					mod.enable();
				
				// Sets the key-bind
				mod.setKeyBind(data.get("key").getAsInt());
				
				//Checks if settings got saved
				if(!data.has("settings"))
					continue;
				
				//Gets the settings
				JsonObject settings = data.get("settings").getAsJsonObject();
				
				//Iterates over all settings from the mod
				for(Setting<?> set : mod.getSettings()) {
					//Checks if a preset exists
					if(!settings.has(set.getName()))
						continue;
					
					//Tries to load the setting
					set.handleParse(settings.get(set.getName()).getAsString());
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			// If anything went wrong, save the current configuration
			this.save();
		}
	}

	/**
	 * Executes the given event on every active module
	 * 
	 * @param event
	 *            the event
	 */
	public void executeEvent(Consumer<Module> event) {
		// Creates a clone and executes the event
		new ArrayList<>(this.getEnabledModules()).forEach(event::accept);
	}

	/**
	 * Executes when a module gets toggled Changes the module state and puts it into
	 * the right list
	 *
	 * @param mod
	 *            the module that got changed
	 */
	public void handleModUpdate(Module mod) {
		// Checks if the module is active
		if (mod.isActive()) {
			// Updates the lists
			this.disabledModules.remove(mod);
			this.enabledModules.add(mod);
		} else {
			// Updates the lists
			this.disabledModules.add(mod);
			this.enabledModules.remove(mod);			
		}
		
		//Saves the module config
		this.save();
		
		//Updates the info display
		this.updateInfoDisplay();
	}

	/**
	 * Handler for the ingame render tick
	 */
	public void handleRender(MatrixStack ms) {
		// Counter for the rendering
		int counter = 0;

		// Checks if any module stats are existing
		if (this.moduleStats != null)
			// Renders all stat-infos
			for (String[] info : this.moduleStats) {
				// For all info's
				for (String text : info) {
					// Renders the info
					Minecraft.getInstance().fontRenderer.drawStringWithShadow(ms,text, 10, (counter += 10), 0xffFFFFFF);
				}

				// Skips the counter to the next module
				counter += 5;
			}
	}

	/**
	 * Returns all modules from that category
	 *
	 * @param category
	 *            the category
	 */
	public List<Module> getModulesByCategory(ModuleCategory category) {
		// Gets all modules
		return this.getModules().stream()
				// Checks if the category's are equal
				.filter(i -> i.getCategory().equals(category))
				// Collects them
				.collect(Collectors.toList());
	}

	/**
	 * Returns a module by it's class
	 *
	 * @param clazz
	 *            the class
	 */
	@SuppressWarnings("unchecked")
	public <T extends Module> Optional<T> getModuleByClass(Class<T> clazz) {
		// Gets all modules
		return this.registeredModules.stream()
				// Checks for the class
				.filter(i -> i.getClass().equals(clazz))
				// Casts it to the requested
				.map(i -> (T) i)
				// Returns that module
				.findAny();
	}

	/**
	 * Returns a module by it's name
	 *
	 * @param name
	 *            the name to search for the module
	 */
	public Optional<Module> getModuleByName(String name) {
		// Gets all modules
		return this.registeredModules.stream()
				// Checks for the name
				.filter(i -> i.getName().equalsIgnoreCase(name))
				// Returns that module
				.findAny();
	}

	/**
	 * Updates the info display
	 */
	public void updateInfoDisplay() {
		// Gets all active modules
		this.moduleStats = this.enabledModules.stream()
				// Gets the display information
				.map(i -> {
					String[] data = Arrays.stream(i.onInformationEvent(Toolsmod.COLOR_MAIN, Toolsmod.COLOR_SECONDARY))
							// Checks if any data got parsed
							.filter(x -> x != null)
							// Converts the information
							.toArray(String[]::new);

					// Checks if any data got parsed
					if (data.length <= 0)
						return null;

					// Creates the new array
					String[] all = new String[data.length + 1];
					// Appends the module name
					all[0] = "§c" + i.getName();
					// Appends the rest of the information
					System.arraycopy(data, 0, all, 1, data.length);
					return all;
				})
				// Checks that the module has elements
				.filter(i -> i != null)
				// Collects all infos
				.toArray(String[][]::new);
	}

	public List<Module> getDisabledModules() {
		return this.disabledModules;
	}

	public List<Module> getEnabledModules() {
		return this.enabledModules;
	}

	public List<Module> getModules() {
		return this.registeredModules;
	}
}

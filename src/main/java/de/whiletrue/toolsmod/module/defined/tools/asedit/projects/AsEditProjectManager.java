package de.whiletrue.toolsmod.module.defined.tools.asedit.projects;

import java.io.File;
import java.util.Optional;

import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.module.defined.tools.asedit.AsEditManager;
import de.whiletrue.toolsmod.module.defined.tools.asedit.EditableArmorStand;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

public class AsEditProjectManager {
	
	//Folder to save the projects
	public static final String SAVE_LOCATION = "assaves";
	
	//Reference to the asedit manager
	private AsEditManager manager;
	
	// Current loaded project
	private AsEditProject loaded;
	
	public AsEditProjectManager(AsEditManager managerRef) {
		this.manager=managerRef;
	}
	
	/**
	 * @param project
	 * @param stands
	 * @return
	 */
	public boolean saveNewProject(AsEditProject project,EditableArmorStand[] stands){
		//Position relative to save the project to
		BlockPos relative = Minecraft.getInstance().player.getPosition();
		
		//Tries to save the project
		if(EnumProjectVersion.NEWEST.getLoader().saveProject(project, stands, project.getFile(), relative)) {
			this.setLoaded(project);
			return true;
		}
		return false;
	}
	
	/**
     * Saves the stands as a new project
     * @param project the project file
     * @return if the save was successful
     */
    public boolean saveNewProject(AsEditProject project) {
    	//Saves the project
    	boolean saved = this.saveNewProject(project, (EditableArmorStand[]) this.manager.getStands().toArray(new EditableArmorStand[this.manager.getStands().size()]));
    	
    	//Loads the project
    	if(saved)
    		this.setLoaded(project);
		return saved;
    }

	/**
	 * @param file
	 *            the file of which the loader is searched
	 * @return optionally the loader for the given file
	 */
	public Optional<EnumProjectVersion> getLoaderForFile(File file) {
		// Gets the extension
		String split[] = file.getName().split("\\.");
		String extension = split[split.length - 1];
		
		// Gets the project version
		return EnumProjectVersion.getFromExtension(extension);
	}
	
	/**
     * Tries to load a project
     * @param project the project
     * @return if the loading was successful
     */
    public boolean loadProject(AsEditProject project) {
    	//Loads the project
    	EditableArmorStand[] stands = project.loadProject();
    	
    	//Checks if the load failed
    	if(stands==null)
    		return false;
    	
    	//Sets the project
    	this.setLoaded(project);
    	
    	//Sets the new stands
    	this.manager.deleteAllStandsNoCheck();
    	this.manager.addAll(stands);
    	
    	//Selects all stands
    	for(EditableArmorStand stand : stands)
    		stand.setSelected(true);
    	this.manager.handleSelect(stands.length==1?0:-1);
    	
    	return true;
    }
    
    public Optional<String> saveProject() {
    	return this.loaded.saveProject((EditableArmorStand[]) this.manager.getStands().toArray(new EditableArmorStand[this.manager.getStands().size()]));
    }

	public boolean isLoaded() {
		return this.loaded != null;
	}

	public void setLoaded(AsEditProject loaded) {
		this.loaded = loaded;

		//Updates the info display
		Toolsmod.getInstance().getModuleManager().updateInfoDisplay();
	}

	public AsEditProject getLoaded() {
		return this.loaded;
	}
}

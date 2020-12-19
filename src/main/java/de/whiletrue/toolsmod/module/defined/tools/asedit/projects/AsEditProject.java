package de.whiletrue.toolsmod.module.defined.tools.asedit.projects;

import java.io.File;
import java.util.Optional;

import javax.annotation.Nullable;

import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.module.defined.tools.asedit.EditableArmorStand;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

public class AsEditProject {
	//File of the project
	private File file;
	
	//Author of the project (10 characters max)
	private String author;
	
	//Project-name (20 characters max)
	private String name;
	
	//Project-version (5 characters max)
	private String projectVersion;
	
	//Version of the project-loader
	private EnumProjectVersion loaderVersion;
	
	/**
	 * Creates a new project with only the file name and the name
	 * @param fileName the file name
	 * @param name the name
	 */
	public AsEditProject(String fileName,String name) {
		this.file = new File(Toolsmod.ID+'/'+AsEditProjectManager.SAVE_LOCATION+'/',fileName+'.'+EnumProjectVersion.NEWEST.getExtension());
		this.author=Minecraft.getInstance().getSession().getUsername();
		this.loaderVersion=EnumProjectVersion.NEWEST;
		this.name=name;
		this.projectVersion="v1";
	}
	
	public AsEditProject(File file,String name,String author,String projectVersion,EnumProjectVersion version) {
		this.file=file;
		this.author=author;
		this.loaderVersion=version;
		this.name=name;
		this.projectVersion=projectVersion;
	}
	
	/**
	 * Updates the project's properties and saves the stands
	 * 
	 * @return optionally the error-code if anything went wrong
	 */
	public Optional<String> saveProject(EditableArmorStand[] stands) {
		// Tries to save the project
		boolean saved = this.getVersion().getLoader().saveProject(this, stands, this.getFile(),
				Minecraft.getInstance().player.getPosition());

		return Optional.ofNullable(saved ? null : "module.asedit.general.project.save.error");
	}

	/**
	 * Converts a project to the newest version
	 * @param project the project to convert
	 * @return optionally an error if the conversion failed
	 */
	public Optional<String> convertProject() {
		// Relative block-position
		BlockPos pos = new BlockPos(0, 0, 0);

		// Loads the project
		Optional<EditableArmorStand[]> optStands = this.getVersion().getLoader().loadProject(this.getFile(), pos);

		// Checks if the project could be loaded
		if (!optStands.isPresent())
			return Optional.of("modules.asedit.gui.projectsselect.convert.load");

		// Gets the new file
		File file = this.getNextFile();

		// Converts the project
		EnumProjectVersion.NEWEST.getLoader().saveProject(this, optStands.get(), file, pos);

		//Deletes the previous file
		boolean del = this.getFile().delete();
		
		if(del) {
			//Updates the file
			this.file=file;
			//Updates the version
			this.loaderVersion=EnumProjectVersion.NEWEST;
			//Exits without and error
			return Optional.empty();
		}
		
		//Exits with an error
		return Optional.of("modules.asedit.gui.projectsselect.convert.failed");
	}
	
	/**
	 * Tries to load the given project
	 * 
	 * @param project the project that should be loaded
	 * @return the stands that got loaded
	 */
	@Nullable
	public EditableArmorStand[] loadProject() {

		// Load the project
		Optional<EditableArmorStand[]> optLoad = this.getVersion().getLoader().loadProject(this.getFile(),
				Minecraft.getInstance().player.getPosition());

		// Checks if the loading was successful
		if (!optLoad.isPresent() || optLoad.get().length <= 0)
			return null;

		// Exits without an error
		return optLoad.get();
	}
	
	/**
	 * @return the next avaiable file for the project
	 */
	public File getNextFile() {
		//Counter
		int c=0;
		
		//Gets the file path
		String path = this.getFile().getAbsolutePath().substring(0,this.getFile().getAbsolutePath().length()-this.getFile().getName().length());
	
		//Gets the file name
		String name = this.getFile().getName().substring(0,this.getFile().getName().length()-this.getVersion().getExtension().length()-1);
		
		//Gets the newest version
		EnumProjectVersion v = EnumProjectVersion.NEWEST;
		
		//Finds a file that does'nt exists yet
		while(c<20000) {
			//Gets the file
			File file = new File(path,name+(c==0?"":String.valueOf(c))+"."+v.getExtension());
			
			//Checks if the file exists
			if(!file.exists())
				return file;
			c++;
		}
		
		//Returns the first file
		return new File(path,name+"."+v.getExtension());
	}
	
	/**
	 * Reloads all properties
	 */
	public void reload() {
		//Loads the project
		Optional<AsEditProject> project = this.getVersion().getLoader().loadProperties(this.file);
		
		//Updates the properties
		if(project.isPresent()) {
			AsEditProject proj = project.get();
			this.name=proj.getName(-1);
			this.author=proj.getAuthor(-1);
			this.projectVersion=proj.getVersion(-1);
		}
	}
	
	/**
	 * Deletes the project from the disk
	 * @return if the deletion was successful
	 */
	public boolean delete() {
		return this.file.delete();
	}
	
	public File getFile() {
		return this.file;
	}
	public EnumProjectVersion getVersion() {
		return this.loaderVersion;
	}
	/**
	 * @param length
	 * @return the name with a fixed length
	 */
	public String getName(int length) {
		return length!=-1 && this.name.length()>=length?this.name.substring(0,length-2)+"...":this.name;
	}
	/**
	 * @param length
	 * @return the author with a fixed length
	 */
	public String getAuthor(int length) {
		return length!=-1 && this.author.length()>=length?this.author.substring(0,length-2)+"...":this.author;
	}
	/**
	 * @param length
	 * @return the author with a fixed length
	 */
	public String getVersion(int length) {
		return length!=-1 && this.projectVersion.length()>=length?this.projectVersion.substring(0,length-2)+"...":this.projectVersion;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setProjectVersion(String projectVersion) {
		this.projectVersion = projectVersion;
	}
	
	public static class InvalidArmorStandProject extends AsEditProject{
		public InvalidArmorStandProject(File file) {
			super(file, null,null,null,null);
		}
	}
}

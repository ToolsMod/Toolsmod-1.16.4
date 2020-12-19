package de.whiletrue.toolsmod.module.defined.tools.asedit.projects.types;

import java.io.File;
import java.util.Optional;

import de.whiletrue.toolsmod.module.defined.tools.asedit.EditableArmorStand;
import de.whiletrue.toolsmod.module.defined.tools.asedit.projects.AsEditProject;
import net.minecraft.util.math.BlockPos;

public interface IProjectVersion {
	/**
	 * Tries to load the project-properties like author and name from the given file
	 * @param file the file to load from
	 * @return the project
	 */
	public abstract Optional<AsEditProject> loadProperties(File file);
	
	/**
	 * Loads all stands from a project
	 * @param file the file to load from
	 * @param relative the position to load it relative to
	 * @return the project's armor stand's
	 */
	public abstract Optional<EditableArmorStand[]> loadProject(File file,BlockPos relative);
	
	/**
	 * Saves the project to the given file
	 * @param project the project-properties
	 * @param stand all stands
	 * @param file the file to save to
	 * @returns if the file already exists
	 */
	public abstract boolean saveProject(AsEditProject project,EditableArmorStand[] stands,File file,BlockPos relative);
}

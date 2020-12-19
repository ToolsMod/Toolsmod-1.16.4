package de.whiletrue.toolsmod.module.defined.tools.asedit.projects;

import java.util.Arrays;
import java.util.Optional;

import de.whiletrue.toolsmod.module.defined.tools.asedit.projects.types.AsEditPVersion1;
import de.whiletrue.toolsmod.module.defined.tools.asedit.projects.types.AsEditPVersion2;
import de.whiletrue.toolsmod.module.defined.tools.asedit.projects.types.IProjectVersion;

public enum EnumProjectVersion {

	JSON_VERSION("json","Json",new AsEditPVersion1()),
	LINE_VERSION("v1","Line",new AsEditPVersion2());
	
	//Newest version
	public final static EnumProjectVersion NEWEST = EnumProjectVersion.LINE_VERSION;
	
	//Extension of the version
	private String extension;
	
	//Version-nickname
	private String nickName;
	
	//Importer to load/save the version
	private IProjectVersion projectVersion;
	
	private EnumProjectVersion(String extension,String nickName,IProjectVersion projectVersion) {
		this.extension=extension;
		this.projectVersion=projectVersion;
		this.nickName=nickName;
	}
	
	/**
	 * @param extension the extension to search for
	 * @return optionally the loader version that fits the given extension
	 */
	public static Optional<EnumProjectVersion> getFromExtension(String extension) {
		
		//Searches for a usable version
		return Arrays.stream(EnumProjectVersion.values())
				//Filters for the extension
				.filter(i->i.getExtension().equalsIgnoreCase(extension))
				//Gets it
				.findAny();
	}
	
	public boolean isOutdated() {
		return !this.equals(EnumProjectVersion.NEWEST);
	}
	
	public String getExtension() {
		return this.extension;
	}
	
	public IProjectVersion getLoader() {
		return this.projectVersion;
	}
	
	public String getNickName() {
		return this.nickName;
	}
	
}

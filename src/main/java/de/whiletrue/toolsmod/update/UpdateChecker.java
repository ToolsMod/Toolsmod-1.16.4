package de.whiletrue.toolsmod.update;

import java.util.Optional;

import de.whiletrue.toolsmod.mod.ModSettings;
import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.util.classes.FileUtil;

public class UpdateChecker {

	//Version updater url
	private final String versionUrl = "https://toolsmod.github.io/api/versions/version-1-16";
	
	//Remote version
	private float remote;
	
	//Loads the changelog
	private Changelog changelog = new Changelog();
	
	public UpdateChecker() {
		//Checks if the remote version should be loaded
		if(ModSettings.checkForRemoteVersion.value)
			//Loads the remote version
			this.remote=this.loadRemoteVersion();
		else
			this.remote=Toolsmod.MOD_VERSION;
	}
	
	/**
	 * Catches the newest version
	 * @return the version or -1 if anything goes wrong
	 */
	private float loadRemoteVersion() {
		//Gets the content
		Optional<String> cont = FileUtil.getInstance().loadRemoteFile(this.versionUrl);
		
		try{
			//Tries to parse the content to a double
			return Float.valueOf(cont.get());
		}catch(Exception e) {
			//Something went wrong while checking version
			return -1;
		}
	}	
	
	public float getRemote() {
		return this.remote;
	}
	public Changelog getChangelog() {
		return this.changelog;
	}
	
}

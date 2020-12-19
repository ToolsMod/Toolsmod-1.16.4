package de.whiletrue.toolsmod.update;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.StreamSupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.util.classes.FileUtil;
import net.minecraft.util.ResourceLocation;

public class Changelog {

	// Date formatter
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("MM/dd/yyyy");

	// Release date of the version corresponding to this change log
	private Date releaseDate;

	//Entries
	private LogEntry[] entries;
	
	public Changelog() {
		// Location of the changelog
		ResourceLocation rsc = new ResourceLocation(Toolsmod.ID, "changelog.json");

		try {
			// Tries to load the changelog data
			JsonObject log = FileUtil.getInstance().loadFromRSCAsJson(rsc).get().getAsJsonObject();

			// Parses the release date
			this.releaseDate = DATE_FORMATTER.parse(log.get("release").getAsString());

			// Gets the changes
			JsonArray changes = log.get("changes").getAsJsonArray();

			// Gets the changes as the stream
			LogEntry[] entries = StreamSupport.stream(changes.spliterator(), false).map(i -> {
				try {
					// Gets the object
					JsonObject val = i.getAsJsonObject();

					// Creates the log
					return new LogEntry(
						val.get("feature").getAsString(),
						val.get("desc").getAsString(),
						ChangeType.getFromId(val.get("type").getAsInt())
					);
				} catch (Exception e) {
					return null;
				}
			})
			//Removes the invalid logs
			.filter(i->i!=null)
			//Collects them
			.toArray(LogEntry[]::new);
			
			//Checks if any invalid logs have been found
			if(entries.length!=changes.size())
				throw new Exception("Invalid entrys found: "+entries.length+'/'+changes.size());

			//Updates the entries
			this.entries=entries;
			
		} catch (Exception e) {
			System.out.println("Failed to load the change log: "+e.getMessage());
		}
	}

	/**
	 * @return if the changelog has been loaded successfully
	 */
	public boolean isLoaded() {
		return this.entries != null;
	}
	
	public LogEntry[] getEntries() {
		return this.entries;
	}
	public String getReleaseDate() {
		return DATE_FORMATTER.format(this.releaseDate);
	}
	
	
}

package de.whiletrue.toolsmod.update;

public class LogEntry {

	//Name and description of the entry
	private String name,description;
	
	//Type of change
	private ChangeType type;
	
	public LogEntry(String name,String desc,ChangeType type) {
		this.name=name;
		this.description=desc;
		this.type=type;
	}
	
	public String getDescription() {
		return this.description;
	}
	public String getName() {
		return this.name;
	}
	public ChangeType getType() {
		return this.type;
	}
	
}

package de.whiletrue.toolsmod.update;

public enum ChangeType {

	REMOVE(0,"Removed",0xff5555),
	ADD(1,"Added",0x55ff55),
	EDIT(2,"Reworked",0xffaa00);
	
	//Id of the type
	private int id;
	
	//Description of the type
	private String description;
	
	//Color of the type
	private int color;
	
	private ChangeType(int id,String description,int color) {
		this.id=id;
		this.description=description;
		this.color=color | (0xff<<24);
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getColor() {
		return this.color;
	}
	
	/**
	 * @param id the id to search for
	 * @return the changetype corresponding to the given id
	 */
	public static ChangeType getFromId(int id) {
		//Iterates over all types
		for(ChangeType c : ChangeType.values())
			//Checks if the id matches
			if(c.id==id)
				return c;
		
		//Default returns removed
		return ChangeType.REMOVE;
	}
}

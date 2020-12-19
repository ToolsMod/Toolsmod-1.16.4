package de.whiletrue.toolsmod.command;

public class CommandError {

	private String key;
	private Object[] items;
	
	private CommandError(String key, Object... items) {
		this.key = key;
		this.items = items;
	}
	
	private CommandError() {}
	
	/*
	 * Checks if the returned error is fine
	 * */
	public boolean isFine() {
		return this.key==null;
	}
	
	/*
	 * Returns the given error key
	 * */
	public String getKey() {
		return this.key;
	}
	
	/*
	 * Returns the given error values
	 * */
	public Object[] getItems() {
		return this.items;
	}
	
	/*
	 * Returns an instance of cmd-error
	 * */
	public static CommandError of(String key, Object... items) {
		return new CommandError(key,items);
	}
	
	/*
	 * Returns a empty error
	 * */
	public static CommandError none() {
		return new CommandError();
	}
	
}

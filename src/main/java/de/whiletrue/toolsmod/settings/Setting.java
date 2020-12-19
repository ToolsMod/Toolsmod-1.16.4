package de.whiletrue.toolsmod.settings;

import javax.annotation.Nullable;

import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.settings.views.SettingView;

public abstract class Setting<T>{
	
	//Holds the value (Should be public for access purpose)
	public T value;
	
	//Name of the setting
	private String name;
	
	//If the setting is invisible
	private boolean invisible;
	
	/**
	 * Sets the name
	 */
	@SuppressWarnings("unchecked")
	public<X extends Setting<T>> X name(String name){
		this.name=name;
		return (X)this;
	}

	/**
	 * Sets the default value
	 */
	@SuppressWarnings("unchecked")
	public<X extends Setting<T>> X standard(T value){
		this.value=value;
		return (X)this;
	}

	/**
	 * Makes the setting invisible
	 */
	@SuppressWarnings("unchecked")
	public<X extends Setting<T>> X hidden(){
		this.invisible=true;
		return (X)this;
	}
	
	
	/**
	 * @param value the value as object
	 * @return the value as a string to save
	 */
	public abstract String handleSave();
	
	/** 
	 * @param value the value as string
	 * @return if the parsing was successful
	 */
	public abstract boolean handleParse(String value);
	
	
	/**
	 * @param mod the module of which the setting is based
	 * 
	 * @return the view to edit the setting
	 */
	public abstract<X extends Setting<T>> SettingView<X> getView(@Nullable Module mod);
	
	public boolean isInvisible() {
		return this.invisible;
	}
	
	public String getName() {
		return this.name;
	}
}

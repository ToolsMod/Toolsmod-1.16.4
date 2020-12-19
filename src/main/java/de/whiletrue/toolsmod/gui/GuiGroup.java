package de.whiletrue.toolsmod.gui;

import net.minecraft.client.Minecraft;

public class GuiGroup<T extends TmScreen>{

	//All added screen from the group
	private T[] screens;
	
	//Currently select screen
	private int index;
	
	private GuiGroup(T[] screens) {
		this.screens=screens;
	}
	
	@SafeVarargs
	public static<T extends TmScreen> GuiGroup<T> of(T... screens) {
		return new GuiGroup<T>(screens);
	}
	
	public T getSelected() {
		return this.screens[this.index];
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public int getSize() {
		return this.screens.length;
	}
	
	public T getGui(int index) {
		return this.screens[index];
	}
	
	/**
	 * Opens the current selected gui
	 */
	public void open() {
		//Updates all other guis
		for (T t : this.screens)
			t.onGroupGuiUpdate(this);
		
		//Opens the gui
		this.update();
	}
	
	/**
	 * Updates the gui without updating the whole group
	 */
	public void update() {
		//Reopens the gui
		Minecraft.getInstance().displayGuiScreen(this.getSelected());
	}
	
	/**
	 * Updates the currently open gui
	 * @param index the index of the new gui
	 */
	public void update(int index) {
		this.index=index;
		this.update();
	}
	
	public boolean isEmpty() {
		return this.getSize()<=0;
	}
	
	/**
	 * Adds the all guis as buttons to the current using callbacks
	 * @param onAdd the callback to add the gui
	 */
	public void addToScreen(IHandleAppend<T> onAdd) {
		for(int i=0;i<this.screens.length;i++)
			onAdd.execute(this.screens[i], this.screens.length, i);
	}
	
	public interface IHandleAppend<T extends TmScreen>{
		public void execute(T item,int size,int current);
	}
}

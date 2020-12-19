package de.whiletrue.toolsmod.gui.widgets.rounding.listmultirow;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.widgets.preset.TmWidget;
import de.whiletrue.toolsmod.util.classes.RenderUtil;
import net.minecraft.client.Minecraft;

public class MultirowListItem<T>{

	//The item
	protected final T item;
	
	//All widgets from the item
	protected final List<TmWidget> widgets = new ArrayList<TmWidget>();
	
	//Game reference
	protected static final Minecraft GAME = Minecraft.getInstance();
	
	//Renderer reference
	protected static final RenderUtil RENDERER = RenderUtil.getInstance();
	
	//Index in the list
	protected int index;
	
	//Current position
	protected int x,y,width,height;
	
	public MultirowListItem(T item) {
		this.item=item;
	}
	
	/*
	 * Handles the change of the view position
	 */
	public void handlChangePosition(int x,int y,int w,int h) {
		this.x=x;
		this.y=y;
		this.width=w;
		this.height=h;
	}
	
	/**
	 * Handles when the list size changes
	 * @param size the new list size
	 */
	public void handleListSizeChange(int size) {}
	
	/**
	 * Renders all widgets
	 * 
	 * @param mX mouse x position
	 * @param mY mouse y position
	 * @param ticks the render ticks
	 * @param focused which widget is currently focused
	 */
	public void render(MatrixStack ms,int mX, int mY, float ticks,TmWidget focused) {
		this.widgets.forEach(i->i.onRender(ms,mX, mY, ticks,focused));
	}

	/**
	 * Post render method executor
	 * Wont be included in the screen cut
	 * 
	 * @param mX the mouse x position
	 * @param mY the mouse y position
	 */
	public boolean onPostRender(MatrixStack ms,int mX,int mY) {
		//Iterates over every widget and execute the event
		for(int i=0;i<this.widgets.size();i++)
			if(this.widgets.get(i).onPostRender(ms,mX, mY))
				return true;
		return false;
	}
	
	/**
	 * Handles the mouse click on every widget
	 * @param mX mouse x position
	 * @param mY mouse y position
	 * @param modifiers modifiers
	 * @return
	 */
	public TmWidget onMouseClicked(double mX,double mY,int modifiers) {
		//Goes over all widgets
		for(int i=this.widgets.size()-1;i>=0;i--) {
			
			//Checks if that or any sub-widgets for focused
			Optional<TmWidget> widget = this.widgets.get(i).onMouseClicked(mX, mY, modifiers);
			
			if(widget.isPresent())
				return widget.get();
		}
		
		return null;
	}

	/**
	 * Handles the tick update event on every widget
	 */
	public void onTick() {
		this.widgets.forEach(i->i.onTick());
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public T getItem() {
		return this.item;
	}
	
}

package de.whiletrue.toolsmod.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.widgets.preset.TmWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class TmScreen extends Screen{

	//All widgets
	private List<TmWidget> widgets = new ArrayList<TmWidget>();
	
	//If the current widget is dragged
	private boolean dragging;
	
	//Currently focused widget
	protected TmWidget focused;
	
	protected TmScreen() {
		super(new StringTextComponent(""));
	}

	protected TmScreen(ITextComponent titleIn) {
		super(titleIn);
	}
	
	@Override
	public void init(Minecraft p_init_1_, int p_init_2_, int p_init_3_) {
		this.widgets.clear();
		super.init(p_init_1_, p_init_2_, p_init_3_);
	}
	
	/**
	 * Adds the widget to the screen
	 * @param widget the widget to add
	 * @return the fluid-api reference
	 */
	protected TmScreen addWidget(TmWidget widget) {
		//Adds the widget
		this.widgets.add(widget);
		//Sends the widget update
		widget.onInitUpdate();
		return this;
	}
	
	/**
	 * Removes the given widget from the list
	 * @param widget the widget
	 */
	protected void removeWidget(TmWidget widget) {
		this.widgets.remove(widget);
	}
	
	/**
	 * @param mX the x mouse position
	 * @param mY the y mouse position
	 * @return the widget that has it's coordinates at position mX and mY
	 */
	private Optional<TmWidget> getWidgetForPos(double mX,double mY){
		for(int i=this.widgets.size()-1;i>=0;i--) {
			TmWidget tm = this.widgets.get(i);
			if(tm.isMouseOver(mX,mY))
				return Optional.of(tm);
		}
		
		return Optional.empty();
	}
	
	@Override
	public void render(MatrixStack ms,int mX, int mY, float ticks){
		//Renders all widgets
		this.widgets.forEach(i->i.onRender(ms,mX, mY, ticks,this.focused));
		
		//Post-renders all widgets
		for(TmWidget w : this.widgets)
			if(w.onPostRender(ms,mX, mY))
				return;
	}
	
	@Override
	public boolean mouseClicked(double mX, double mY, int ticks) {
		//Resets the focused element
		this.focused=null;
		
		//Handles the event on all widgets
		for(int i=this.widgets.size()-1;i>=0;i--) {
			//Gets the clicked widget
			Optional<TmWidget> w = this.widgets.get(i).onMouseClicked(mX, mY, ticks);
			
			//Checks if any widget for clicked
			if(w.isPresent()) {
				//Focuses the widget
				this.focused=w.get();
				
				//Checks if that widget got dragged
				if(ticks==0)
					this.dragging=true;
				
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean mouseDragged(double mX, double mY, int arg3,
			double arg4, double arg5) {		
		return this.focused!=null && this.dragging && arg3 == 0 ? this.focused.onMouseDragged(mX, mY, arg3, arg4, arg5) : false;
	}
	
	@Override
	public boolean mouseReleased(double mX, double mY, int ticks) {
		this.dragging=false;		
		return this.getWidgetForPos(mX, mY).filter(i->i.onMouseReleased(mX, mY, ticks)).isPresent();
	}
	
	@Override
	public boolean mouseScrolled(double mX, double mY, double strength) {
		return this.getWidgetForPos(mX, mY).filter(i->i.onMouseScrolled(mX, mY, strength)).isPresent();
	}
	
	@Override
	public boolean keyPressed(int keyCode,int scanCode,int modifiers) {
		
		//Checks if the focused widget used the event
		if(this.focused != null && this.focused.onKeyPressed(keyCode, scanCode, modifiers))
			return true;
		//Check if ESC got pressed
		else if(keyCode == 256) {
			//Closes the gui
			this.closeScreen();
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return this.focused != null && this.focused.onKeyReleased(keyCode, scanCode, modifiers);
	}
	
	@Override
	public boolean charTyped(char key, int keyCode) {
		return this.focused != null && this.focused.onCharTyped(key, keyCode);
	}
	
	@Override
	public boolean isPauseScreen() {
		return false;
	}
	
	/**
	 * Gets called when a gui group opens where this gui is included
	 * @param group the group
	 */
	public void onGroupGuiUpdate(GuiGroup<? extends TmScreen> group) {}
	
	@Override
	public void tick() {
		this.widgets.forEach(i->i.onTick());
	}
	
	public TmWidget getFocusedWidget() {
		return this.focused;
	}
	
	/**
	 * Unfocuses the current widget
	 */
	public void unFocuse() {
		this.focused=null;
	}
}

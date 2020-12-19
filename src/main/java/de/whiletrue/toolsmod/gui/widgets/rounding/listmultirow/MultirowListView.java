package de.whiletrue.toolsmod.gui.widgets.rounding.listmultirow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.widgets.preset.TmSizeWidget;
import de.whiletrue.toolsmod.gui.widgets.preset.TmWidget;
import de.whiletrue.toolsmod.gui.widgets.rounding.EnumListSlider;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.math.MathHelper;

public class MultirowListView<ItemView extends MultirowListItem<?>> extends TmSizeWidget{

	//How far the list got scrolled (Pixels)
	private int scrolled;
	
	//Formatting of the list
	//Space between items (Pixel)
	private int spaceX,
				spaceY;
	//Amount of items in one row
	private int itemsPerRow;
	
	//General list height. All items with the spaces not stopped by the listview height
	private int itemHeight;
	
	//Scroll strength multiplier
	private float scrollStrength = 1;
	
	//Where the slider is placed
	private EnumListSlider slider = EnumListSlider.RIGHT;
	
	//Background-color
	private int background = -1;
	
	//Validator
	private Function<ItemView, Boolean> validator = null;
	
	//[Unmodifiable] Slider width
	private int sliderWidth = 6;
	
	//Calculated itemWidth
	private int itemWidth;
	
	//All added items
	private List<ItemView> views = new ArrayList<ItemView>();
	
	//Slider dragging offset
	private int dragOffset;
	
	//Current slider height
	private int sliderHeight;
	
	//Calculates the list height
	private int listHeight;
	
	//Holds all indexes to the views that are on screen. If there are not enough views, the index will be -1
	private int[] viewIndexes;
	
	public MultirowListView(int x, int y, int width, int height) {
		super(x,y,width,height);
		//Sets the default list formatting
		this.setListFormatting(0, 5, 1, 30);
	}

	public MultirowListView<ItemView> setListFormatting(int spaceX,int spaceY,int itemsPerRow,int itemHeight){
		this.spaceX=spaceX;
		this.spaceY=spaceY;
		this.itemsPerRow=itemsPerRow;
		this.itemHeight=itemHeight;
		
		//Calculates all values that depend on the list format
		this.calculateViewIndexes();
		this.calculateItemWidth();
		this.calculateListHeight();
		
		//Updates the items and scroll-slider
		this.updateItems();
		this.updateScroll(this.scrolled);
		return this;
	}
	public MultirowListView<ItemView> setScrollStrength(float scrollStrength) {
		this.scrollStrength = scrollStrength;
		return this;
	}
	public MultirowListView<ItemView> setSlider(EnumListSlider slider) {
		this.slider = slider;
		return this;
	}
	public MultirowListView<ItemView> setBackground(int background) {
		this.background=background;
		return this;
	}
	public MultirowListView<ItemView> setItems(@SuppressWarnings("unchecked") ItemView... items){
		//Adds the views
		this.views.clear();
		this.views.addAll(Arrays.asList(items));
		this.updateListIndexes();
		
		//Updates the items and scroll-slider
		this.calculateListHeight();
		this.updateScroll(this.scrolled);
		this.updateItems();
		return this;
	}
	public MultirowListView<ItemView> setValidator(Function<ItemView, Boolean> validator) {
		this.validator = validator;
		return this;
	}
	public void addItem(ItemView item){
		//Adds the item
		this.views.add(item);
		//Updates the list indexes
		this.updateListIndexes();
		
		//Updates the items and scroll-slider
		this.calculateListHeight();
		this.updateScroll(this.scrolled);
		this.updateItems();
	}
	public void remove(ItemView item) {
		//Removes the item
		this.views.remove(item);
		//Updates all list indexes
		this.updateListIndexes();
		
		//Updates the items and scroll-slider
		this.calculateListHeight();
		this.updateScroll(this.scrolled);
		this.updateItems();
	}
	public void updateListIndexes() {
		//Updates the index
		for(int i=0;i<this.views.size(); i++) {
			this.views.get(i).setIndex(i);
			this.views.get(i).handleListSizeChange(this.views.size());
		}
	}
	
	private void calculateViewIndexes() {
		this.viewIndexes=new int[this.height/(this.itemHeight+this.spaceY)*this.itemsPerRow+this.itemsPerRow*2];
	}
	
	private void calculateItemWidth() {
		this.itemWidth=((this.width-this.spaceX*(this.itemsPerRow-1))/this.itemsPerRow);
	}
	
	/**
	 * Call this when a new validation state has occurred
	 */
	public void updateValidation() {
		this.calculateListHeight();
		this.updateScroll(this.scrolled);
		this.updateItems();
	}
	
	/**
	 * Updates the scroll-bar
	 * @param newScroll the new scrolled position
	 */
	public void updateScroll(int newScroll) {
		
		//Checks if the list even has to scroll
		if(this.listHeight>this.height)
			//Scrolls the list and ensures that the scroll don't overflows
			this.scrolled = MathHelper.clamp(
				newScroll,
				0, 
				this.listHeight-this.height
			);
		else
			this.scrolled=0;
		
		//Calculates the slider height
		this.sliderHeight = Math.max(20,(int) (this.height*((float)this.height/(float)this.listHeight)));
	}
	
	private void calculateListHeight() {
		
		//Amount of views that matching the criteria of the validator
		int working = 0;
		
		//Calculates the amount of items that are validated by the calculator
		for(int i=0;i<this.views.size();i++) {
			//Updates the item
			if(this.validator==null||this.validator.apply(this.views.get(i)))
				working++;
		}
		
		//Calculates the actual list height
		this.listHeight = (this.itemHeight+this.spaceY)*((int)Math.ceil((float)working/(float)this.itemsPerRow))-this.spaceY;
	}
	
	/**
	 * Updates all item positions
	 */
	public void updateItems() {
		
		//Current index in the views list
		int cur = 0;
		//Amount of items that have matched the validator criteria
		int working=0;
		
		//Gets the viewindexes
		for(int i=0;i<this.viewIndexes.length;i++) {
			//Resets the current index
			this.viewIndexes[i]=-1;
			
			//Gets the new index, if one exists
			while(cur<this.views.size()) {
				
				//Gets the item
				ItemView iv = this.views.get(cur);
				
				//Checks if the validator matches that item
				if(this.validator==null||this.validator.apply(iv)) {
					//Calculates the y position
					int y = this.y+(this.itemHeight+this.spaceY)*(working/this.itemsPerRow)-this.scrolled;
					
					//Checks if the item is on screen
					if(y+this.itemHeight>this.y) {
						
						//Uses that item
						this.viewIndexes[i]=cur;
						int x = this.x+(this.itemWidth+this.spaceX)*(working%this.itemsPerRow);
						
						//Updates the item position
						iv.handlChangePosition(x, y, this.itemWidth, this.itemHeight);
						working++;
						cur++;
						break;
					}
					working++;
				}
				cur++;
			}	
		}
	}
	
	/**
	 * @return the sliders y position
	 */
	private int getSliderY() {
		return (int) (this.y + (this.height-this.sliderHeight) * ((float)this.scrolled/(float)(this.listHeight-this.height)));
	}
	
	
	@Override
	public void onRender(MatrixStack ms,int mX, int mY, float ticks,TmWidget focused) {
		super.onRender(ms,mX, mY, ticks, focused);

		//Checks if the list is hovered
		if(!this.hovered) {
			//Changes the mouse position to prevent hovering over items
			mX=this.x-5;
			mY=this.y-5;
		}

		//Renders the background
		if(this.background!=-1)
			AbstractGui.fill(ms,this.x,this.y,this.x+this.width,this.y+this.height,this.background);
		//Clips out the list render
		this.renderer.startScissor(this.x,this.y,this.width,this.height);
		{
			//Renders all view indexes
			for(int i : this.viewIndexes)
				if(i!=-1)
					this.views.get(i).render(ms,mX, mY, ticks, focused);
		}
		this.renderer.stopScissor();


		//Checks if the slider is enabled
		if(this.listHeight>this.height && !this.slider.equals(EnumListSlider.NONE)) {
			//Slider x position
			int sX = this.x+(this.slider.equals(EnumListSlider.LEFT)?-this.sliderWidth:this.width);
			//Slider y position
			int sY = this.getSliderY();

			//Renders the dark background
			this.renderer.renderRect(ms,sX,this.y,this.sliderWidth,this.height,0xff000000);
			
			//Renders the slider outline
			this.renderer.renderRect(
				ms,
				sX,
				sY,
				this.sliderWidth,
				this.sliderHeight,
				0xFF808080
			);
			//Renders the slider background
			this.renderer.renderRect(
				ms,
				sX,
				sY,
				this.sliderWidth-1,
				this.sliderHeight-1,
				0xFFc0c0c0
			);
		}
	}
	
	@Override
	public boolean onPostRender(MatrixStack ms,int mX, int mY) {
		//Checks if the list is hovered
		if(!this.hovered)
			return false;
		
		//Renders the post event
		for(int i : this.viewIndexes)
			if(i != -1 && this.views.get(i).onPostRender(ms,mX, mY))
				return true;
		return false;
	}
	
	@Override
	public void move(float... size) {
		super.move(size);
		this.calculateViewIndexes();
		this.calculateItemWidth();
		this.updateScroll(this.scrolled);
		this.updateItems();
	}

	@Override
	public Optional<TmWidget> onMouseClicked(double mX, double mY, int ticks) {
		
		//Checks if the slider is enabled
		if(this.listHeight>this.height && !this.slider.equals(EnumListSlider.NONE)) {			
			//Calculates the y position
			int sliderY = this.getSliderY();
			
			//Gets the sliders x position
			int sliderX = this.x+(this.slider.equals(EnumListSlider.LEFT)?-this.sliderWidth:this.width);
			
			//Checks if the slider is hovered
			if(mX>=sliderX && mX <= sliderX+this.sliderWidth && mY>=sliderY && mY <= sliderY+this.sliderHeight) {
				
				//Drags the list (Slider)
				this.dragOffset = (int) (mY-sliderY);
				return Optional.of(this);
			}
		}
		
		//Checks if the list is hovered
		if(!this.hovered)
			return Optional.empty();
		
		//Handles the click event on all views
		for(int i : this.viewIndexes) {
			if(i==-1)
				continue;
			TmWidget wid = this.views.get(i).onMouseClicked(mX, mY, ticks);
			if(wid!=null)
				return Optional.of(wid);
		}
		
		return Optional.empty();
	}
	
	@Override
	public boolean onMouseDragged(double mX, double mY, int arg2, double arg3, double arg4) {
		//Percentage of the mouse y on the list
		double perc = MathHelper.clamp((double)(mY-this.dragOffset-this.y)/(double)(this.height-this.sliderHeight), 0d, 1d);

		//Updates the scroll-slider
		this.updateScroll((int) ((this.listHeight-this.height)*perc));
		
		//Updates the list
		this.updateItems();
		
		return true;
	}
	
	@Override
	public boolean onMouseScrolled(double mX, double mY, double strength) {
		//Updates the scroll-slider
		this.updateScroll((int)(this.scrolled-strength*this.scrollStrength));
		
		//Updates the item's position
		this.updateItems();
		return true;
	}
	
	@Override
	public void onTick() {
		this.views.forEach(i->i.onTick());
	}

	public List<ItemView> getViews() {
		return this.views;
	}
	
	public int getScrolled() {
		return this.scrolled;
	}
	
	/**
	 * Only clears the list, wont recalculate anything or check anything
	 */
	public void clearWithoutCheck() {
		this.views.clear();
	}
}

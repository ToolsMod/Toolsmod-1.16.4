package de.whiletrue.toolsmod.gui.widgets.rounding.listscaleable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.widgets.preset.TmSizeWidget;
import de.whiletrue.toolsmod.gui.widgets.preset.TmWidget;
import de.whiletrue.toolsmod.gui.widgets.rounding.EnumListSlider;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.math.MathHelper;

public class ScaleableListView<ItemView extends ScaleableListItem<?>> extends TmSizeWidget {

	// Scroller values
	private int scrolled; // How far the list got scrolled (Pixels)
	private float scrollStrength = 1; // Scroller strength multiplier
	private EnumListSlider slider = EnumListSlider.RIGHT; // Slider position and if it exists
	private final int sliderWidth = 6; // [Unmodifiable] Slider width
	private int sliderDragOffset; // Slider dragging offset
	private int sliderHeight; // Current slider height
	
	// List formatting
	private int spaceY; // Space on the y axis between the items

	private List<ItemView> views = new ArrayList<ItemView>() // Item view's of the list
			, visibleViews = new ArrayList<>(); // List with all views
												// that match the
												// validation criteria

	// General list stuff
	private int listHeight; // The height of all views that are currently available (Visible)

	// Style
	private int background = -1; // Background of the whole list

	// Validator
	private Function<ItemView, Boolean> validator = null;

	public ScaleableListView(int x, int y, int width, int height) {
		super(x, y, width, height);

		this.setSpaceY(5);
	}

	@Override
	public void onRender(MatrixStack ms,int mX, int mY, float ticks, TmWidget focused) {
		super.onRender(ms,mX, mY, ticks, focused);

		// Checks if the list is hovered
		if (!this.hovered) {
			mX = this.x - 5;
			mY = this.y - 5;
		}

		// Renders the background
		if (this.background != -1)
			AbstractGui.fill(ms,this.x, this.y, this.x + this.width, this.y + this.height, this.background);

		// Clips out the list render
		this.renderer.startScissor(this.x, this.y, this.width, this.height);
		{
			// Iterates over all visible views
			for (ItemView iv : this.visibleViews) {
				// Checks if the following views would render outside of the list
				if (iv.y > this.y + this.height)
					break;

				// Checks if the view is on screen
				if (iv.y + iv.getHeight() < this.y)
					continue;

				// Renders the view
				iv.render(ms,mX, mY, ticks, focused);
			}
		}
		this.renderer.stopScissor();

		// Checks if the slider is enabled
		if (this.listHeight > this.height && !this.slider.equals(EnumListSlider.NONE)) {
			// Slider x position
			int sX = this.x + (this.slider.equals(EnumListSlider.LEFT) ? -this.sliderWidth : this.width);
			// Slider y position
			int sY = this.getSliderY();

			// Renders the dark background
			this.renderer.renderRect(ms,sX, this.y, this.sliderWidth, this.height, 0xff000000);

			// Renders the slider outline
			this.renderer.renderRect(ms,sX, sY, this.sliderWidth, this.sliderHeight, 0xFF808080);
			// Renders the slider background
			this.renderer.renderRect(ms,sX, sY, this.sliderWidth - 1, this.sliderHeight - 1, 0xFFc0c0c0);
		}
	}

	@Override
	public boolean onPostRender(MatrixStack ms,int mX, int mY) {
		// Checks if the list is hovered
		if (!this.hovered)
			return false;

		// Renders the post event
		for (ItemView iv : this.visibleViews) {
			// Checks if the following views would render outside of the list
			if (iv.y > this.y + this.height)
				break;

			// Checks if the view is on screen
			if (iv.y + iv.getHeight() < this.y)
				continue;

			if (iv.onPostRender(ms,mX, mY))
				return true;
		}
		return false;
	}

	@Override
	public Optional<TmWidget> onMouseClicked(double mX, double mY, int ticks) {

		// Checks if the slider is enabled
		if (this.listHeight > this.height && !this.slider.equals(EnumListSlider.NONE)) {
			// Calculates the y position
			int sliderY = this.getSliderY();

			// Gets the sliders x position
			int sliderX = this.x + (this.slider.equals(EnumListSlider.LEFT) ? -this.sliderWidth : this.width);

			// Checks if the slider is hovered
			if (mX >= sliderX && mX <= sliderX + this.sliderWidth && mY >= sliderY
					&& mY <= sliderY + this.sliderHeight) {

				// Drags the list (Slider)
				this.sliderDragOffset = (int) (mY - sliderY);
				return Optional.of(this);
			}
		}

		// Checks if the list is hovered
		if (!this.hovered)
			return Optional.empty();

		// Handles the click event on all views
		for (ItemView iv : this.visibleViews) {
			// Checks if the following views would render outside of the list
			if (iv.y > this.y + this.height)
				break;

			// Checks if the view is on screen
			if (iv.y + iv.getHeight() < this.y)
				continue;

			TmWidget wid = iv.onMouseClicked(mX, mY, ticks);
			if (wid != null)
				return Optional.of(wid);
		}

		return Optional.empty();
	}

	@Override
	public boolean onMouseDragged(double mX, double mY, int arg2, double arg3, double arg4) {
		// Percentage of the mouse y on the list
		double perc = MathHelper.clamp(
				(double) (mY - this.sliderDragOffset - this.y) / (double) (this.height - this.sliderHeight), 0d, 1d);

		// Updates the scroll-slider
		this.updateScroll((int) ((this.listHeight - this.height) * perc));

		// Updates the list
		this.updateItems();

		return true;
	}

	@Override
	public boolean onMouseScrolled(double mX, double mY, double strength) {
		// Updates the scroll-slider
		this.updateScroll((int) (this.scrolled - strength * this.scrollStrength));

		// Updates the item's position
		this.updateItems();
		return true;
	}

	@Override
	public void onTick() {
		this.views.forEach(i -> i.onTick());
	}

	@Override
	public void move(float... size) {
		super.move(size);
		
		//Updates the width for all items
		this.views.forEach(i->{
			//Updates the width and gets the new height
			int h = i.handleChangePosition(this.x, this.width);
			
			//Checks if a new height got set
			if(h>0)
				//Updates the height
				i.setHeight(h);
		});
		
		this.calculateListHeight();
		this.updateScroll(this.scrolled);
		this.updateItems();
	}

	/**
	 * Handles the event when an item changes it's height
	 */
	public void handleHeightChange() {
		// Recalculates the full list height
		this.calculateListHeight();
		// Updates the scroller
		this.updateScroll(this.scrolled);
		// Updates the items positions
		this.updateItems();
	}

	public ScaleableListView<ItemView> setSpaceY(int spaceY) {
		this.spaceY = spaceY;
		return this;
	}

	public ScaleableListView<ItemView> setItems(@SuppressWarnings("unchecked") ItemView... items) {
		// Sets all items
		this.views.clear();
		this.views.addAll(Arrays.asList(items));
		// Inits the views
		this.views.forEach(i -> i.handlePostInit(this::handleHeightChange,this.x,this.width));
		// Updates the validation for the new items
		this.updateValidation();
		return this;
	}
	
	public ScaleableListView<ItemView> setScrollStrength(float scrollStrength) {
		this.scrollStrength = scrollStrength;
		return this;
	}
	public ScaleableListView<ItemView> setBackground(int background) {
		this.background = background;
		return this;
	}
	public ScaleableListView<ItemView> setValidator(Function<ItemView, Boolean> validator) {
		this.validator = validator;
		return this;
	}

	private void calculateListHeight() {

		// Calculates the height of all views
		int hV = this.visibleViews.size() > 0
				? this.visibleViews.stream().map(i -> i.getHeight()).reduce((a, b) -> a + b).get()
				: 0;

		// Calculates the actual list height
		this.listHeight = this.spaceY * (this.visibleViews.size() - 1) + hV;
	}

	/**
	 * Updates all item positions
	 */
	public void updateItems() {
		// Total height of all previous views
		int usedHeight = 0;

		// Iterates over all valid views
		for (int i = 0; i < this.visibleViews.size(); i++) {
			// Gets the item
			ItemView iv = this.visibleViews.get(i);

			// Gets the y position
			int y = this.y + usedHeight + this.spaceY * i - this.scrolled;
			// Updates the height
			usedHeight += iv.getHeight();

			// Updates the item and gets the optional new height
			iv.handleChangePositionScrolled(y);
		}
		
	}

	/**
	 * Updates the scroll-bar
	 * 
	 * @param newScroll
	 *            the new scrolled position
	 */
	public void updateScroll(int newScroll) {

		// Checks if the list even has to scroll
		if (this.listHeight > this.height)
			// Scrolls the list and ensures that the scroll don't overflows
			this.scrolled = MathHelper.clamp(newScroll, 0, this.listHeight - this.height);
		else
			this.scrolled = 0;

		// Calculates the slider height
		this.sliderHeight = Math.max(20, (int) (this.height * ((float) this.height / (float) this.listHeight)));
	}

	public void updateValidation() {
		// Clears all visible views
		this.visibleViews.clear();

		// Checks if the validator exists
		if (this.validator == null)
			this.visibleViews.addAll(this.views);
		else
			// Appends all views that matched the validator
			this.visibleViews.addAll(this.views.stream().filter(this.validator::apply).collect(Collectors.toList()));

		// Recalculates the list height
		this.calculateListHeight();
		// Updates the scroller
		this.updateScroll(this.scrolled);
		// Updates the items
		this.updateItems();
	}

	/**
	 * @return the sliders y position
	 */
	private int getSliderY() {
		return (int) (this.y + (this.height - this.sliderHeight)
				* ((float) this.scrolled / (float) (this.listHeight - this.height)));
	}

	public List<ItemView> getViews() {
		return this.views;
	}

	public int getScrolled() {
		return this.scrolled;
	}
}

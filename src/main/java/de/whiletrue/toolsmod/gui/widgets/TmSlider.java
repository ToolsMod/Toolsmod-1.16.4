package de.whiletrue.toolsmod.gui.widgets;

import java.util.Optional;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.widgets.preset.TmSizeWidget;
import de.whiletrue.toolsmod.gui.widgets.preset.TmWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class TmSlider extends TmSizeWidget{

	//Values of the slider
	private float min,max,percCurrent;
	
	//Event handler
	protected IDragable onDrag;
	
	//Display string
	protected String display="";
	
	public TmSlider(int x, int y, int width, int height,float minValue,float maxValue,float currentValue,IDragable onDrag) {
		super(x, y, width, height);
		
		this.onDrag=onDrag;
		this.min=minValue;
		this.max=maxValue;
		this.setByValue(currentValue);
		this.setDisplay(onDrag.execute(this,currentValue));
	}
	
	
	@Override
	public void onInitUpdate() {
		//Execute the drag event and update the display
		this.updateDisplay();
	}
	
	public TmSlider setByValue(float currentValue) {
		this.percCurrent = (float)(currentValue-this.min)/(float)(this.max-this.min);
		return this;
	}
	public TmSlider setDragHandler(IDragable onDrag) {
		this.onDrag = onDrag;
		return this;
	}
	
	public float getState() {
		return this.min+(float)(this.max-this.min)*this.percCurrent;
	}
	
	@Override
	public Optional<TmWidget> onMouseClicked(double mX, double mY, int ticks) {
		
		if(this.hovered)
			//Handles the point-set
			this.onMouseDragged(mX, mY, 0, 0, 0);
		
		return super.onMouseClicked(mX, mY, ticks);
	}
	
	@Override
	public boolean onMouseDragged(double mX, double mY, int arg2, double arg3, double arg4) {
		//Update the percentage value
		this.percCurrent = (float)(MathHelper.clamp((int)mX, this.x, this.x+this.width)-this.x)/(float)this.width;
		
		//Execute the drag event and update the display
		this.updateDisplay();
		
		return super.onMouseDragged(mX, mY, arg2, arg3, arg4);
	}
	
	@Override
	public void onRender(MatrixStack ms,int mX, int mY, float ticks,TmWidget focused) {
		super.onRender(ms,mX, mY, ticks,focused);
		

		//Renders the slider background
		GuiUtils.drawContinuousTexturedBox(Widget.WIDGETS_LOCATION, this.x, this.y, 0, 46, this.width, this.height, 200, 20, 2, 3, 2, 2, -90);
        
		//Renders the slider-drop
        GuiUtils.drawContinuousTexturedBox(Widget.WIDGETS_LOCATION, this.x + (int)(this.percCurrent * (float)(this.width - 8)), this.y, 0, 66, 8, this.height, 200, 20, 2, 3, 2, 2, -90);

        //Gets the text color
        int color = this.hovered?0xFFffffa0 : 0xFFffffff;
        
        if(this.display!=null)
        	this.renderer.renderCenteredString(ms,this.display, this.x+this.width/2, this.y+this.height/2-4, color);
        
	}
	
	/**
	 * Updates the display of the slider
	 */
	public void updateDisplay() {
		//Executes the drag event
		this.setDisplay(this.onDrag.execute(this, this.getState()));
	}
	
	public void setDisplay(String display) {
		this.display = display;
	}

	public interface IDragable{
		public String execute(TmSlider slider,float value);
	}
}

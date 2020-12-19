package de.whiletrue.toolsmod.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.widgets.preset.TmSizeWidget;
import de.whiletrue.toolsmod.gui.widgets.preset.TmWidget;
import de.whiletrue.toolsmod.util.classes.RenderUtil;

public class TmBackgroundWidget extends TmSizeWidget{

	//Reference to the renderer
	private RenderUtil renderer = RenderUtil.getInstance();
	
	//Color of the widget
	private int color;
	
	//Optional outline
	private int outlineColor=-1;
	//Optional outline strength
	private int outlineStrength;
	
	public TmBackgroundWidget(int x,int y,int width,int height,int color) {
		super(x,y,width,height);
		this.color=color;
	}
	
	public TmBackgroundWidget setOutline(int color,int strength) {
		this.outlineColor=color;
		this.outlineStrength=strength;
		return this;
	}
	public TmBackgroundWidget setColor(int color) {
		this.color = color;
		return this;
	}
	
	@Override
	public void onRender(MatrixStack ms,int mX, int mY, float ticks,TmWidget focused) {
		super.onRender(ms,mX, mY, ticks,focused);
		//Renders the rect
		this.renderer.renderRect(ms,this.x, this.y, this.width, this.height, this.color);
		
		//Renders the outline
		if(this.outlineColor!=-1)
			this.renderer.renderOutline(ms,this.x, this.y, this.width, this.height, this.outlineStrength, this.outlineColor);
	}
}

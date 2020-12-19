package de.whiletrue.toolsmod.gui.widgets.preset;

import java.util.Optional;

import com.mojang.blaze3d.matrix.MatrixStack;

public abstract class TmSizeWidget extends TmWidget{

	//Size of the widget
	protected int x,y,width,height;

	//If the widget is hovered
	protected boolean hovered;
	
	public TmSizeWidget(int x,int y,int width,int height) {
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
	}
	
	@Override
	public void onRender(MatrixStack ms,int mX, int mY, float ticks,TmWidget focused) {
		this.hovered=this.isMouseOver(mX, mY);
	}
	
	@Override
	public boolean isMouseOver(double mX, double mY) {
		return mX>=this.x&&mX<=this.x+this.width && mY>=this.y&&mY<=this.y+this.height;
	}

	@Override
	public void move(float... size) {
		this.x=(int)size[0];
		this.y=(int)size[1];
		this.width=(int)size[2];
		this.height=(int)size[3];
	}
	
	@Override
	public Optional<TmWidget> onMouseClicked(double mX, double mY, int ticks) {
		return Optional.ofNullable(this.hovered?this:null);
	}

	public TmSizeWidget setX(int x) {
		this.x = x;
		return this;
	}
	public TmSizeWidget setY(int y) {
		this.y = y;
		return this;
	}
	public TmSizeWidget setWidth(int width) {
		this.width = width;
		return this;
	}
	public TmSizeWidget setHeight(int height) {
		this.height = height;
		return this;
	}
}

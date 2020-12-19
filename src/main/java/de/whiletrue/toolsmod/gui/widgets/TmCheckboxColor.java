package de.whiletrue.toolsmod.gui.widgets;

import java.util.Optional;
import java.util.function.Function;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.widgets.preset.TmSizeWidget;
import de.whiletrue.toolsmod.gui.widgets.preset.TmWidget;

public class TmCheckboxColor extends TmSizeWidget{

	//If the box is checked
	private boolean checked;

	//Styles
	private int outlineColor,fillColor,outlineStrength;
	
	//Check update event handler
	private Function<Boolean, Boolean> onCheck;

	public TmCheckboxColor(int x, int y, int width, int height,boolean checked,Function<Boolean, Boolean> onCheck) {
		this(x, y, width, height,checked,0xff000000,0xffFF8A17,1,onCheck);
	}
	
	public TmCheckboxColor(int x, int y, int width, int height,boolean checked,int outlineColor,int fillColor,int outlineStrength,Function<Boolean, Boolean> onCheck) {
		super(x, y, width, height);
		this.checked=checked;
		this.onCheck=onCheck;
		this.outlineColor=outlineColor;
		this.fillColor=fillColor;
		this.outlineStrength=outlineStrength;
	}
	
	@Override
	public Optional<TmWidget> onMouseClicked(double mX, double mY, int ticks) {
		//Checks if the box should be updated
		if(this.hovered)
			this.checked=this.onCheck.apply(!this.checked);
		return Optional.empty();
	}
	
	@Override
	public void onRender(MatrixStack ms,int mX, int mY, float ticks, TmWidget focused) {
		super.onRender(ms,mX, mY, ticks, focused);
		
		//Renders the outline
		this.renderer.renderOutline(ms,this.x,this.y,this.width,this.height,this.outlineStrength,this.outlineColor);
		
		//If hovered, render the inside
		if(this.checked) {
			this.renderer.renderRect(
				ms,
				this.x+this.outlineStrength+1,
				this.y+this.outlineStrength+1,
				this.width-this.outlineStrength*2-2,
				this.height-this.outlineStrength*2-2,
				this.fillColor
			);
		}
	}
	
	public boolean isChecked() {
		return this.checked;
	}
	
	public TmCheckboxColor setChecked(boolean checked) {
		this.checked = checked;
		return this;
	}
}

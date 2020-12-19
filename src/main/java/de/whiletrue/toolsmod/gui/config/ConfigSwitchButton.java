package de.whiletrue.toolsmod.gui.config;

import java.util.Optional;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.widgets.TmButton;
import de.whiletrue.toolsmod.gui.widgets.preset.TmWidget;
import de.whiletrue.toolsmod.util.classes.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class ConfigSwitchButton extends TmButton {
	
	//How fast the hover is completed
	private float hoverSpeed = .1f;
	//If the button is constantly underlined
	private boolean constandUnderlined = false;
	//Amount of pixel that should be removed from the bottom and top when hovered (Percentage)
	private float hoverAmount = .25f;
	
	//Style-color
	private int backgroundColor = 0x60aa0000;
	private int lineColor = 0xffaa0000;
	
	//Text of the button
	private String hoverText,normalText;
	
	//Current hover value (Percentage)
	private float hoverPerc=0;

	//Font renderer reference
	private FontRenderer font = Minecraft.getInstance().fontRenderer;
	
	public ConfigSwitchButton(int x, int y, int width, int height, String text, IPressable onPress) {
		super(x, y, width, height,null,onPress);
		
		this.setText(text);
	}
	
	@Override
	public void onRender(MatrixStack ms,int mX, int mY, float ticks, TmWidget focused) {
		//Calculates the amount of pixels that should be removed from the top and bottom
		final int less = (int) (this.height*this.hoverAmount*(1-this.hoverPerc));
		
		//Updates the hovered state
		this.hovered = mX >= this.x && mY >= this.y+less && mX < this.x + this.width && mY < this.y + this.height-less;

		//Updates the hover position
		if(this.hovered || focused == this || this.constandUnderlined) {
			if(this.hoverPerc<1)
				this.hoverPerc+=this.hoverSpeed;
		}else if(this.hoverPerc>=0)
			this.hoverPerc-=this.hoverSpeed;
		
		//Renders the general background
		this.renderer.renderRect(ms,this.x, this.y+less, this.width, this.height-less*2, this.backgroundColor);
		
		//Renders the outer lines
		this.renderer.renderRect(ms,this.x,this.y+less,1,this.height-less*2,this.lineColor);
		this.renderer.renderRect(ms,this.x+this.width-1,this.y+less,1,this.height-less*2,this.lineColor);
		
		//Renders the string
		if(this.hoverText!=null && this.normalText!=null)
			this.renderer.renderCenteredString(ms,this.constandUnderlined||focused == this||this.hovered?this.hoverText:this.normalText, this.x+this.width/2, this.y+this.height/2-this.font.FONT_HEIGHT/2, 0xffffffff);
	}
	
	@Override
	public Optional<TmWidget> onMouseClicked(double mX, double mY, int ticks) {
		if(this.hovered)
			this.onPress.execute(this);
		
		return Optional.ofNullable(this.hovered?this:null);
	}
	
	@Override
	public boolean isMouseOver(double p_isMouseOver_1_, double p_isMouseOver_3_) {
		return this.hovered;
	}

	public ConfigSwitchButton setHoverSpeed(float hoverSpeed) {
		this.hoverSpeed = hoverSpeed;
		return this;
	}

	public ConfigSwitchButton setConstandUnderlined(boolean constandUnderlined) {
		this.constandUnderlined = constandUnderlined;
		return this;
	}

	public ConfigSwitchButton setHoverAmount(float hoverAmount) {
		this.hoverAmount = hoverAmount;
		return this;
	}

	public ConfigSwitchButton setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}

	public ConfigSwitchButton setLineColor(int lineColor) {
		this.lineColor = lineColor;
		return this;
	}

	public ConfigSwitchButton setHoverPerc(float hoverPerc) {
		this.hoverPerc = hoverPerc;
		return this;
	}
	public ConfigSwitchButton setText(String text) {
		if(text!=null) {			
			this.normalText=text;
			this.hoverText="§n"+text;
		}
		return this;
	}
	public ConfigSwitchButton setTextByKey(String key,Object... args) {
		this.setText(TextUtil.getInstance().getByKey(key,args));
		return this;
	}
}

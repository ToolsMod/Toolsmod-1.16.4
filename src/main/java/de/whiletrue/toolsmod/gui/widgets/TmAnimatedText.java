package de.whiletrue.toolsmod.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.widgets.preset.TmWidget;
import de.whiletrue.toolsmod.util.classes.TextUtil;

public class TmAnimatedText extends TmTextWidget{

	//Animation transition
	private long fadeIn,fadeOut,length;
	
	//Starting time
	private long start = -1;
	
	//Color of the widget
	private int textColor;
	
	public TmAnimatedText(int x, int y) {
		super(x, y, null, 0);
	}

	/**
	 * Sets the transitions
	 * @param fadeIn the time it takes to fully display
	 * @param length the time it displays
	 * @param fadeOut the time it takes to fully vanish
	 */
	public TmAnimatedText setTransition(long fadeIn,long length,long fadeOut) {
		this.fadeIn=fadeIn;
		this.length=length;
		this.fadeOut=fadeOut;
		return this;
	}
	
	/**
	 * Displays the given message
	 * @param message the message to display
	 */
	public void show(int color,String message) {
		this.setText(message);
		this.textColor=color&0xffffff;
		this.start=System.currentTimeMillis()-100;
	}
	
	/**
	 * Displays the given message from the language key
	 * @param key the language key
	 */
	public void showKey(int color,String key,Object... args) {
		this.show(color,TextUtil.getInstance().getByKey(key,args));
	}
	
	
	@Override
	public void onRender(MatrixStack ms,int mouseX, int mouseY, float p_render_3_,TmWidget focused) {
		//Checks if an animation is playing
		if(this.start==-1)
			return;
		
		//Checks if the animation is over
		if(this.start+this.length+this.fadeIn+this.fadeOut-100 <= System.currentTimeMillis()) {
			//Reset
			this.start=-1;
			return;
		}
		//Gets the time that has parsed
		long time = System.currentTimeMillis()-this.start;
		
		if(time<=this.fadeIn) {
			//Gets the percentage value
			float perc = (float)time/(float)this.fadeIn;
			//Sets the color with the transparency
			this.color = (((int)(0xff*perc)<<24) | this.textColor );
		}else if(time>=this.fadeIn+this.length) {
			//Gets the percentage value
			float perc = 1-((float)(time-this.fadeIn-this.length)/(float)this.fadeOut);
			//Sets the color with the transparency
			this.color = (((int)(0xff*perc)<<24) | this.textColor);
		}
		
		//Renders the text
		super.onRender(ms,mouseX, mouseY, p_render_3_,focused);
	}

	
}

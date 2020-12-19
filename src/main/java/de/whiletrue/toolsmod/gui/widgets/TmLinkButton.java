package de.whiletrue.toolsmod.gui.widgets;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.util.classes.TextUtil;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;

public class TmLinkButton extends TmButton{

	//The link to the webpage
	private String link;

	//The hover display save
	private String hoverDisplay;
	
	//Scale factor
	private final double factor = .7d;
	
	public TmLinkButton(int x, int y, int width, int height, String display, String link) {
		super(x, y, width, height, display, i->{});
		this.link=link;
		//Updates the display
		this.setDisplay(display);
		//Sets the press info
		this.setOnPress(i->this.openUrl());
		
		//Updates the tooltip
		this.setTooltipp(new StringTextComponent("§7"+link));
	}
	
	@Override
	public TmButton setDisplayByKey(String key, Object... args) {
		return this.setDisplay(TextUtil.getInstance().getByKey(key,args));
	}
	
	@Override
	public boolean onPostRender(MatrixStack ms,int mX, int mY) {
		//Checks if the button is hovered
		if(!this.hovered)
			return false;
		
		GL11.glPushMatrix();
		{
			//Scales the tooltipp
			GL11.glScaled(this.factor, this.factor, 0);
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT); 
			//Renders the tooltipp
			super.onPostRender(ms,(int)(mX*(1/this.factor)), (int)(mY*(1/this.factor)));
		}
		GL11.glPopMatrix();
		
		return true;
	}
	
	/**
	 * Updates the displayed string
	 */
	public TmButton setDisplay(String text) {
		this.display=text;
		this.hoverDisplay="§n"+text;
		return this;
	}
	
	@Override
	protected String getRenderText(boolean focused) {
		return this.hovered?this.hoverDisplay:this.display;
	}
	
	/**
	 * Opens the given url in the default browser
	 * @param url the url to open
	 */
	private void openUrl() {
		//Gets the url
		String url = this.link;
		//Checks if the url is complet
		if(!url.startsWith("http"))
			url="https://"+url;
			
		//Opens the url
        Util.getOSType().openURI(url);
	}
	
}

package de.whiletrue.toolsmod.gui.widgets;

import java.util.Optional;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.widgets.preset.TmSizeWidget;
import de.whiletrue.toolsmod.gui.widgets.preset.TmWidget;
import de.whiletrue.toolsmod.util.TextAlign;
import net.minecraft.util.Util;

public class TmLink extends TmSizeWidget{

	//Link to call
	private String link;
	
	//Reference text to indicate hover
	private String hoverText,text;
	
	//Alignment of the text
	private TextAlign alignX = TextAlign.MIDDLE,alignY=TextAlign.BEFORE;
	
	public TmLink(int x, int y, String msg,String link) {
		super(x, y, 0, 0);
		this.link=link;//Sets the link
		this.setText(msg);//Updates the text
	}
	
	public TmLink setText(String msg) {
		//Updates the hover text
		this.hoverText = "§n"+msg;
		//Sets the text
		this.text=msg;
		
		//Updates the text size
		this.width=this.mc.fontRenderer.getStringWidth(msg);
		this.height=this.mc.fontRenderer.FONT_HEIGHT;
		return this;
	}

	@Override
	public void move(float... size) {
		this.x=(int)size[0];
		this.y=(int)size[1];
	}
	
	@Override
	public void onRender(MatrixStack ms,int mX, int mY, float ticks, TmWidget focused) {
		//Calculates the position of the text
		int x = this.x-(this.alignX.equals(TextAlign.BEFORE)?0:this.alignX.equals(TextAlign.MIDDLE)?this.width/2:this.width);
		int y = this.y-(this.alignY.equals(TextAlign.BEFORE)?0:this.alignY.equals(TextAlign.MIDDLE)?this.height/2:this.height);
		
		//Updates the hover state
		this.hovered=mX>=x&&mX<=x+this.width && mY>=y&&mY<=y+this.height;
		
		//Renders the text
		this.mc.fontRenderer.drawStringWithShadow(ms,this.hovered?this.hoverText:this.text, x, y, 0xff5555ff);
	}
	
	@Override
	public Optional<TmWidget> onMouseClicked(double mX, double mY, int ticks) {
		if(this.hovered)
			this.openUrl();
		return Optional.empty();
	}
	
	public TmLink setAlignX(TextAlign alignX) {
		this.alignX = alignX;
		return this;
	}
	public TmLink setAlignY(TextAlign alignY) {
		this.alignY = alignY;
		return this;
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

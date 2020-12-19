package de.whiletrue.toolsmod.gui.config.sub.info.credits;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.widgets.TmLink;
import de.whiletrue.toolsmod.gui.widgets.preset.TmWidget;
import de.whiletrue.toolsmod.gui.widgets.rounding.listscaleable.ScaleableListItem;
import de.whiletrue.toolsmod.util.TextAlign;
import de.whiletrue.toolsmod.util.classes.TextUtil;

public class CreditView extends ScaleableListItem<Credit>{

	//Gets the splitted string
	private String[] splitted;
	
	//Link to the creator if that exists
	@Nullable
	private TmLink link;
	
	public CreditView(Credit item) {
		super(item);
		
		//Checks if a link is given
		if(item.getLink()!=null)
			//Creates the link
			this.widgets.add(this.link=new TmLink(0, 0, "View more", item.getLink())
				.setAlignX(TextAlign.AFTER));
	}
	
	@Override
	public void handleChangePositionScrolled(int y) {
		super.handleChangePositionScrolled(y);
		//Updates the links position
		if(this.link!=null)
			this.link.move(this.x+this.width,y);
	}
	
	@Override
	public int handleChangePosition(int x, int w) {
		super.handleChangePosition(x, w);
		
		//Updates the link
		if(this.link!=null)
			this.link.move(x+w,this.y);
		
		//Splits the info into lined segments
		this.splitted = TextUtil.getInstance().splitStringOnWidth(this.item.getText(), w-40, w).split("\n");

		//Amount of lines
		int lines = this.splitted.length;
		
		//Calculates width and height
		return (lines*10+5+5);
	}
	
	@Override
	public void render(MatrixStack ms,int mX, int mY, float ticks, TmWidget focused) {
		
		GL11.glPushMatrix();
		{
			//Scales up the author's name
			GL11.glScaled(1.1, 1.1, 0);
			//Renders the author
			GAME.fontRenderer.drawStringWithShadow(ms,this.item.getAuthor(), (float) (this.x/1.1), (float) (this.y/1.1), 0xff04D498);
		}
		GL11.glPopMatrix();
		
		//Renders the text infos
		for(int i=0;i<this.splitted.length;i++)
			GAME.fontRenderer.drawStringWithShadow(ms,this.splitted[i], this.x, this.y+i*10+12, 0xFFffffff);
		
		super.render(ms,mX, mY, ticks, focused);
	}
	
}

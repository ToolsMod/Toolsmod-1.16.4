package de.whiletrue.toolsmod.gui.config.sub.info.changelog;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.widgets.preset.TmWidget;
import de.whiletrue.toolsmod.gui.widgets.rounding.listscaleable.ScaleableListItem;
import de.whiletrue.toolsmod.update.LogEntry;
import de.whiletrue.toolsmod.util.classes.TextUtil;

public class LogView extends ScaleableListItem<LogEntry>{

	//Padding between the values
	private final int PADDING = 20;
	
	//All lines of the description
	private String[] description;
	
	public LogView(LogEntry item) {
		super(item);
	}
	
	@Override
	public int handleChangePosition(int x, int w) {
		super.handleChangePosition(x, w);
		
		//Gets the size of all values
		int nameW = GAME.fontRenderer.getStringWidth(this.item.getName());
		int typeW = GAME.fontRenderer.getStringWidth(this.item.getType().getDescription());
		int descW = w-nameW-typeW-this.PADDING*4;
		
		//Splits the description into multiple lines
		this.description = TextUtil.getInstance().splitStringOnWidth(this.item.getDescription(), descW-60, descW).split("\n");
		
		return GAME.fontRenderer.FONT_HEIGHT*this.description.length;
	}
	
	@Override
	public void render(MatrixStack ms,int mX, int mY, float ticks, TmWidget focused) {
		//Renders the name
		GAME.fontRenderer.drawStringWithShadow(ms,this.item.getName(), this.x+this.PADDING, this.y, 0xFFffffff);
		
		//Renders the status
		GAME.fontRenderer.drawStringWithShadow(
			ms,
			this.item.getType().getDescription(),
			this.x+this.width-this.PADDING-GAME.fontRenderer.getStringWidth(this.item.getType().getDescription()),
			this.y,
			this.item.getType().getColor()
		);
		
		//Renders the description
		for(int i=0;i<this.description.length;i++)
			//Renders the text
			GAME.fontRenderer.drawStringWithShadow(
				ms,
				this.description[i],
				this.x+this.width/2-GAME.fontRenderer.getStringWidth(this.description[i])/2,
				this.y+i*GAME.fontRenderer.FONT_HEIGHT,
				0xFFffffff
			);
	}
}

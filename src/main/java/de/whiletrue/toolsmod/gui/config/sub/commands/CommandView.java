package de.whiletrue.toolsmod.gui.config.sub.commands;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.command.defined.Command;
import de.whiletrue.toolsmod.gui.widgets.TmCheckbox;
import de.whiletrue.toolsmod.gui.widgets.preset.TmWidget;
import de.whiletrue.toolsmod.gui.widgets.rounding.listmultirow.MultirowListItem;

public class CommandView extends MultirowListItem<Command>{

	public CommandView(Command item) {
		super(item);

		//Adds the allowed widget
		this.widgets.add(new TmCheckbox(0, 0, item.isAllowed(), i->{
			item.setAllowed(i);
			return i;
		}).setTooltippByKey("gui.config.commands.allowed"));
	}
	
	@Override
	public void handlChangePosition(int x, int y, int w, int h) {
		super.handlChangePosition(x, y, w, h);
		//Allowed checkbox
		this.widgets.get(0).move(x+w-25,y+h/2-10,20,20);
	}

	@Override
	public void render(MatrixStack ms,int mX, int mY, float ticks, TmWidget focused) {
		// If the element is hovered
		boolean hovered = mX >= this.x && mX <= this.x + this.width && mY > this.y && mY < this.y + this.height;

		// Renders the background
		RENDERER.renderRect(ms,this.x, this.y, this.width, this.height, 0xff8C8C8C);
		// Renders the outline
		RENDERER.renderOutline(ms,this.x, this.y, this.width, this.height, 1, hovered ? 0xffff8f00 : 0xffffffff);

		// Renders the text
		GAME.fontRenderer.drawStringWithShadow(ms,this.item.getName(), this.x + 10, this.y + this.height / 2 - 4,
				0xFFffffff);
		super.render(ms,mX, mY, ticks, focused);
	}

	/**
	 * Checks if this item matches the search criteria
	 * 
	 * @param text
	 *            the text
	 * @return if the item matches
	 */
	public boolean isValid(String text) {
		return this.item.getName().toLowerCase().contains(text.toLowerCase());
	}

}

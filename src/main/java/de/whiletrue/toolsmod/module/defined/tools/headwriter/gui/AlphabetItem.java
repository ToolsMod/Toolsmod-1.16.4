package de.whiletrue.toolsmod.module.defined.tools.headwriter.gui;

import java.util.function.Consumer;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.widgets.preset.TmWidget;
import de.whiletrue.toolsmod.gui.widgets.rounding.listmultirow.MultirowListItem;
import de.whiletrue.toolsmod.module.defined.tools.headwriter.Alphabet;

public class AlphabetItem extends MultirowListItem<Alphabet>{

	//If the current alphabet is the selected
	private boolean select;
	
	//Select handler
	private Consumer<AlphabetItem> onSelect;
	
	public AlphabetItem(Alphabet item,Consumer<AlphabetItem> onSelect) {
		super(item);
		this.onSelect=onSelect;
	}
	
	public AlphabetItem setSelected(boolean select) {
		this.select=select;
		return this;
	}

	@Override
	public void render(MatrixStack ms,int mX, int mY, float ticks, TmWidget focused) {
		//If the item is hovered
		boolean hovered = mX>=this.x&&mX<=this.x+this.width && mY>=this.y&&mY<=this.y+this.height;
		
		//Renders the background
		RENDERER.renderRect(ms,this.x, this.y, this.width, this.height, hovered||this.select?0xff008FFF:0xffee5a24);
		
		//Renders the name
		RENDERER.renderCenteredString(ms,this.item.getName(), this.x+this.width/2, this.y+this.height/2-4, 0xFFffffff);
	}
	
	@Override
	public TmWidget onMouseClicked(double mX, double mY, int modifiers) {
		//If the item is hovered
		boolean hovered = mX>=this.x&&mX<=this.x+this.width && mY>=this.y&&mY<=this.y+this.height;
		
		//Checks if the current item got clicked
		if(hovered)
			this.onSelect.accept(this);
		
		return super.onMouseClicked(mX, mY, modifiers);
	}
	
	/**
	 * Checks if the item is valid for the given search result
	 * @param text the text to match
	 * @return if the item is valid
	 */
	public boolean isValid(String text) {
		return this.item.getName().toLowerCase().contains(text.toLowerCase());
	}
	
}

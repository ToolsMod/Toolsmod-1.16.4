package de.whiletrue.toolsmod.gui.modules;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.widgets.preset.TmWidget;
import de.whiletrue.toolsmod.gui.widgets.rounding.listmultirow.MultirowListItem;
import de.whiletrue.toolsmod.module.defined.Module;

public class ModuleItem extends MultirowListItem<Module>{

	public ModuleItem(Module item) {
		super(item);
	}

	@Override
	public void render(MatrixStack ms,int mX, int mY, float ticks, TmWidget focused) {
		
		//If the item is hovered
		boolean hovered = mX>=this.x&&mX<=this.x+this.width && mY>=this.y&&mY<=this.y+this.height;
		
		//Renders the background
		RENDERER.renderRect(ms,this.x, this.y, this.width, this.height, hovered?0xff008FFF:0xFF000000);
		//Renders the text
		RENDERER.renderCenteredString(ms,this.item.getName(), this.x+this.width/2, this.y+this.height/2-4, this.item.isActive()?0xff1DC617:0xffC61717);
	}
	
	@Override
	public TmWidget onMouseClicked(double mX, double mY, int modifiers) {
		//If the item is hovered
		boolean hovered = mX>=this.x&&mX<=this.x+this.width && mY>=this.y&&mY<=this.y+this.height;

		//Checks if the button is hovered
		if(hovered) {
			//Checks if the click is to activate (LEFT = 0)
			if(modifiers==0)
				this.item.toggle();
			//Checks if the click is to get the settings (RIGHT = 1)
			else if(modifiers == 1)
				//Checks if the item has a qa gui
				if(this.item.getQuickAccessGui() != null)
					this.item.getQuickAccessGui().open();
					
		}
		
		
		return super.onMouseClicked(mX, mY, modifiers);
	}
	
}

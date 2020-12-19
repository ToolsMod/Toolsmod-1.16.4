package de.whiletrue.toolsmod.module.defined.tools.mapcreator.gui;

import java.util.function.Consumer;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.widgets.preset.TmWidget;
import de.whiletrue.toolsmod.gui.widgets.rounding.listmultirow.MultirowListItem;
import de.whiletrue.toolsmod.module.defined.tools.mapcreator.BlockSelection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.ItemStack;

public class BlockSelectionItem extends MultirowListItem<BlockSelection>{

	//If the current item is selected
	private boolean selected;
	
	//Reference to the item stack
	private ItemStack is;
	private IBakedModel model;
	
	//Selection listener
	private Consumer<BlockSelection> onSelect;
	
	public BlockSelectionItem(BlockSelection item,Consumer<BlockSelection> onSelect,boolean selected) {
		super(item);
		this.onSelect=onSelect;
		
		//Gets the item and model
		this.is=new ItemStack(item.getSelected().asItem());
		this.model = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(this.is, null, Minecraft.getInstance().player);
		
		//Updates the selection state
		this.setSelected(selected);
	}
	
	@Override
	public void render(MatrixStack ms,int mX, int mY, float ticks, TmWidget focused) {
		//If the item is focused
		boolean hovered = mX>=this.x&&mX<=this.x+this.width && mY>=this.y&&mY<=this.y+this.height;
		
		//Renders the background
		RENDERER.renderRect(ms,this.x, this.y, this.width, this.height, this.selected?0xffee5a24:hovered?0xff008FFF:0xff707070);
		
		//Renders the selected block
		RENDERER.renderItem(this.is, this.model, this.x+2, this.y+this.height/2-8);
		
		//Renders the color
		RENDERER.renderRect(ms,this.x+this.width-14, this.y+this.height/2-6, 12, 12,this.item.getColor().getRGB());
	}
	
	
	@Override
	public TmWidget onMouseClicked(double mX, double mY, int modifiers) {
		//If the item is focused
		boolean hovered = mX>=this.x&&mX<=this.x+this.width && mY>=this.y&&mY<=this.y+this.height;
		
		if(hovered)
			//Executes the handler
			this.onSelect.accept(this.item);
		
		return super.onMouseClicked(mX, mY, modifiers);
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}

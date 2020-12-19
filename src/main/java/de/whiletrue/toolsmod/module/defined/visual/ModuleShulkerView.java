package de.whiletrue.toolsmod.module.defined.visual;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.whiletrue.toolsmod.module.ModuleCategory;
import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.settings.defined.SettingBool;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModuleShulkerView extends Module{

	//Location of the shulker container
	private ResourceLocation widget_location = new ResourceLocation("textures/gui/container/shulker_box.png");
	
	//If the color should be rendered
	private SettingBool sColor = new SettingBool()
		.name("renderColor")
		.standard(true);
	
	//If the color if the box should render
	private SettingBool sRenderEmpty = new SettingBool()
	.name("renderEmpty")
	.standard(false);
	
	//Last hovered shulker
	private ItemStack lastShulker;
	//Items of the shulker
	private NonNullList<ItemStack> shulkerItems = NonNullList.withSize(27, ItemStack.EMPTY);
	//Color for the shulker stored in R/G/B from 0-1
	private float[] shulkerColor = new float[3];
	
	public ModuleShulkerView() {
		super("ShulkerView", ModuleCategory.VISUALS,true);
		//Registers an extra event
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	void onRenderTooltip(RenderTooltipEvent.PostBackground evt) {
		//Checks if the module is active
		if(!this.isActive())
			return;
		
		//Updates the shulker finder
		this.updateHovered(evt.getStack());
		
		//Checks if the shulker should render
		if(this.lastShulker==null)
			return;
		
		//Size of the gui
		int guiSize = 71;
		
		//Calculates the gui coordinates
		int x = evt.getX()-5;
		int y = (int) (evt.getY()+evt.getHeight()+guiSize>this.mc.currentScreen.height ? evt.getY()-guiSize : evt.getY()+evt.getHeight()+4);
		
		//Renders the gui
		this.renderBackground(evt.getMatrixStack(),x, y);
		//Renders the items
		this.renderItems(x, y);
	}
	
	/**
	 * Updates the current rendered shulker.
	 * If a new one gets hovered, it reads out it's content
	 * @param stack
	 */
	private void updateHovered(ItemStack stack) {
		//Checks if the stack is the current
		if(stack.equals(this.lastShulker))
			return;
		
		//Resets the shulker
		this.lastShulker=null;
		
		//Checks if the item is a block
		if(!(stack.getItem() instanceof BlockItem))
			return;
		
		BlockItem itm = (BlockItem) stack.getItem();
		
		//Checks if the item is a shulker
		if(!(itm.getBlock() instanceof ShulkerBoxBlock))
			return;

		//Checks if the empty box can render and if the box is empty
		if(!this.sRenderEmpty.value && (!stack.hasTag() || stack.getTag().getCompound("BlockEntityTag").getList("Items", 10).isEmpty()))
			return;
		
		//Gets the shulker's color
		DyeColor dye = ((ShulkerBoxBlock)itm.getBlock()).getColor();
		int color = (dye==null?DyeColor.PURPLE:dye).getColorValue();
		
		//Calculates the color values
		this.shulkerColor[0]=(float)((color&0xff<<16)>>16)/255f;
		this.shulkerColor[1]=(float)((color&0xff<<8)>>8)/255f;
		this.shulkerColor[2]=(float)(color&0xff)/255f;
		
		//Sets the new box
		this.lastShulker=stack;
		this.shulkerItems.clear();
		
		//Checks if the shulker has items
		if(stack.hasTag())
			//Gets all items
			ItemStackHelper.loadAllItems(stack.getTag().getCompound("BlockEntityTag"),this.shulkerItems);
	}
	
	/**
	 * Renders the shulkers items
	 * @param x the x position to render
	 * @param y the y position to render
	 */
	private void renderItems(int x,int y) {		
		//Prevents other items from rendering over
		this.mc.getItemRenderer().zLevel=300;
		
		//Render all items
		for(int i=0;i<this.shulkerItems.size();i++) {
			
			//Gets the item
			ItemStack itm = this.shulkerItems.get(i);
			
			//Calculates the item position
			int itmX = x+(i%9*18)+8;
			int itmY = y+(i/9)*18+8;
			
			//Renders the item
			this.mc.getItemRenderer().renderItemAndEffectIntoGUI(itm, itmX,itmY);
			this.mc.getItemRenderer().renderItemOverlays(this.mc.fontRenderer, itm, itmX, itmY);
		}
	}
	
	/**
	 * Renders the shulker background menu in the shulker's color
	 * @param x the x position to render
	 * @param y the y position to render
	 */
	private void renderBackground(MatrixStack ms,int x,int y) {
		//Disables the depth testing
		RenderSystem.disableDepthTest();
		//Bind the gui texture
		this.mc.textureManager.bindTexture(this.widget_location);

		//Checks if color should be rendered
		if(this.sColor.value)
			//Colors the gui
			GL11.glColor3f(this.shulkerColor[0], this.shulkerColor[1], this.shulkerColor[2]);
		
		//Renders the gui-background
		AbstractGui.blit(ms,x, y, -90, 0, 0, 176, 5, 256, 256);
		AbstractGui.blit(ms,x, y+5, -90, 0, 15, 176, 3 * 18 + 2, 256, 256);
		AbstractGui.blit(ms,x, y+5+3*18+2, -90 , 0, 160, 176, 6, 256, 256);
	}
}

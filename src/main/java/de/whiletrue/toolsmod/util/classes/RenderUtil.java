package de.whiletrue.toolsmod.util.classes;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

public class RenderUtil {

	private static RenderUtil instance;
	
	//Reference to the game
	private Minecraft mc = Minecraft.getInstance();
	
	private RenderUtil() {}

	public static RenderUtil getInstance() {
		if(instance==null)
			instance=new RenderUtil();
		return instance;
	}
	
	/**
     * Renders an outline with the given property's
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width
     * @param height the height
     * @param outlineStrength the outline strength
     * @param outlineColor the outline color
     */
    public void renderOutline(MatrixStack ms,int x,int y,int width,int height,int outlineStrength,int outlineColor) {
		//Upper
		this.renderRect(
				ms,
				x,
				y,
				width,
				outlineStrength,
				outlineColor
		);
		//Lower
		this.renderRect(
				ms,
				x,
				y+height-outlineStrength,
				width,
				outlineStrength,
				outlineColor
		);
		//Right
		this.renderRect(
				ms,
				x+width-outlineStrength,
				y+outlineStrength,
				outlineStrength,
				height-outlineStrength*2,
				outlineColor
		);
		//Left
		this.renderRect(
				ms,
				x,
				y+outlineStrength,
				outlineStrength,
				height-outlineStrength*2,
				outlineColor
		);
	}
	
	/**
	 * Starts scissors on the given rect
	 * Everything that gets rendered outside this rect wont get rendered
	 * 
	 * @param x the x position 
	 * @param y the y position
	 * @param width the width
	 * @param height the height
	 */
	public void startScissor(int x,int y,int width,int height) {
		//Gets the window
		MainWindow win = this.mc.getMainWindow();
		//Gets the scale factor
		double scale = win.getGuiScaleFactor();
		
		//Starts the scissors
		GL11.glScissor(
			(int) (x*scale),
			(int)((win.getScaledHeight()-y-height)*scale),
			(int) (width*scale),
			(int) (height*scale)
		);
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
	}
	
	/**
	 * Stops the scissors inited from {@link #startScissor(int, int, int, int)}
	 */
	public void stopScissor() {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
	
	/**
	 * Renders a rect from x,y with the w,h size
	 * 
	 * @param x the starting x position
	 * @param y the starting y position
	 * @param w the width
	 * @param h the height
	 * @param color the color
	 */
	public void renderRect(MatrixStack ms,int x,int y,int w,int h,int color) {
		AbstractGui.fill(ms,x,y,x+w,y+h,color);
	}
	
	/**
	 * Renders the default background from y to the height
	 * @param y the y position
	 * @param height the height
	 */
	public void renderDirtBackground(int y,int height) {
		int width = this.mc.currentScreen.width;
		Tessellator tessellator = Tessellator.getInstance();
	      BufferBuilder bufferbuilder = tessellator.getBuffer();
	      this.mc.getTextureManager().bindTexture(AbstractGui.BACKGROUND_LOCATION);
	      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
	      bufferbuilder.pos(0.0D, (double)height+y, 0.0D).tex(0.0F, (float)(height) / 32.0F).color(64, 64, 64, 255).endVertex();
	      bufferbuilder.pos((double)width, (double)y+height, 0.0D).tex((float)width / 32.0F, (height) / 32.0F + (float)0).color(64, 64, 64, 255).endVertex();
	      bufferbuilder.pos((double)width, y, 0).tex((float)width / 32.0F, (float)0).color(64, 64, 64, 255).endVertex();
	      bufferbuilder.pos(0, y, 0).tex(0F, (float)0).color(64, 64, 64, 255).endVertex();
	      tessellator.draw();
	}
	
	/**
	 * Renders the given string centered to x
	 * @param text the text to render
	 * @param x the x position
	 * @param y the y position
	 * @param color the color of the text
	 */
	public void renderCenteredString(MatrixStack ms,String text,int x,int y,int color) {
		this.mc.fontRenderer.drawStringWithShadow(ms,text, x-this.mc.fontRenderer.getStringWidth(text)/2, y, color);
	}
	
	/**
     * Shorter way to render an item-stack on the screen
     *
     * @param item the item to render
     * @param x the x position
     * @param y the y position
     * @param model the model of the item so it wont have to get searched ever render time
     */
    public void renderItem(ItemStack item,IBakedModel model, int x, int y) {
    	RenderSystem.pushMatrix();
    	{
    		this.mc.getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
    		this.mc.getTextureManager().getTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE).setBlurMipmapDirect(false, false);
    		RenderSystem.enableRescaleNormal();
    		RenderSystem.enableAlphaTest();
    		RenderSystem.defaultAlphaFunc();
    		RenderSystem.enableBlend();
    		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    		RenderSystem.translatef((float)x, (float)y, 100.0F + this.mc.getItemRenderer().zLevel);
    		RenderSystem.translatef(8.0F, 8.0F, 0.0F);
    		RenderSystem.scalef(1.0F, -1.0F, 1.0F);
    		RenderSystem.scalef(16.0F, 16.0F, 16.0F);
    		MatrixStack matrixstack = new MatrixStack();
    		IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
    		boolean flag = !model.isSideLit();
    		if (flag)
    			RenderHelper.setupGuiFlatDiffuseLighting();
    		
    		this.mc.getItemRenderer().renderItem(item, ItemCameraTransforms.TransformType.GUI, false, matrixstack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, model);
    		irendertypebuffer$impl.finish();
    		if (flag)
    			RenderHelper.setupGui3DDiffuseLighting();
    		
    		RenderSystem.disableAlphaTest();
    		RenderSystem.disableRescaleNormal();
    	}
        RenderSystem.popMatrix();
    }
    
    /**
     * Calculates the axis aligned bb form the given positions
     */
    public AxisAlignedBB calculateAxisAlign(double x,double y,double z,double sizeX,double sizeY,double sizeZ) {
    	//Gets the render-position
    	Vector3d pos = this.mc.worldRenderer.renderDispatcher.getRenderPosition();
    	//Creates the coordinate
    	return new AxisAlignedBB(
    			x-pos.getX(),
    			y-pos.getY(),
    			z-pos.getZ(),
    			x-pos.getX()+sizeX,
    			y-pos.getY()+sizeY,
    			z-pos.getZ()+sizeZ);
    }
    
    /**
     * Renders a block-overlay at the given position
     * @param x position on the x axis
     * @param y position on the y axis
     * @param z position on the z axis
     */
    public void renderBlockOverlay(double x,double y,double z,double size) {
    	//Gets the render-position
    	Vector3d pos = this.mc.worldRenderer.renderDispatcher.getRenderPosition();
    	
    	//Creates the coordinate
    	AxisAlignedBB coord = new AxisAlignedBB(
    			x-pos.getX()+.5-.5*size,
    			y-pos.getY()+.5-.5*size,
    			z-pos.getZ()+.5-.5*size,
    			x-pos.getX()+1*size,
    			y-pos.getY()+1*size,
    			z-pos.getZ()+1*size);
    	
    	//Renders the overlay
    	this.renderBlockOverlay(coord);
    }
    
    public void renderBlockOverlay(double minX,double minY,double minZ,double maxX,double maxY,double maxZ) {
    	//Gets the render-position
    	Vector3d pos = this.mc.worldRenderer.renderDispatcher.getRenderPosition();
    	
    	//Min values
    	double iX = Math.min(minX,maxX)-pos.x;
    	double iY = Math.min(minY,maxY)-pos.y;
    	double iZ = Math.min(minZ,maxZ)-pos.z;
    	
    	//Max values
    	double aX = Math.max(minX,maxX)-pos.x;
    	double aY = Math.max(minY,maxY)-pos.y;
    	double aZ = Math.max(minZ,maxZ)-pos.z;
    	
    	Tessellator ts = Tessellator.getInstance();
        BufferBuilder wr = ts.getBuffer();
        wr.begin(7, DefaultVertexFormats.POSITION);
        wr.pos(iX, iY, iZ).endVertex();
        wr.pos(iX, aY, iZ).endVertex();
        wr.pos(aX, aY, iZ).endVertex();
        wr.pos(aX, iY, iZ).endVertex();
        ts.draw();
        wr.begin(7, DefaultVertexFormats.POSITION);
        wr.pos(iX, aY, iZ).endVertex();
        wr.pos(iX, aY, aZ).endVertex();
        wr.pos(aX, aY, aZ).endVertex();
        wr.pos(aX, aY, iZ).endVertex();
        ts.draw();
        wr.begin(7, DefaultVertexFormats.POSITION);
        wr.pos(iX, iY, iZ).endVertex();
        wr.pos(iX, iY, aZ).endVertex();
        wr.pos(iX, aY, aZ).endVertex();
        wr.pos(iX, aY, iZ).endVertex();
        ts.draw(); 
        wr.begin(7, DefaultVertexFormats.POSITION);
        wr.pos(iX, iY, iZ).endVertex();
        wr.pos(aX, iY, iZ).endVertex();
        wr.pos(aX, iY, aZ).endVertex();
        wr.pos(iX, iY, aZ).endVertex();
        ts.draw();
        wr.begin(7, DefaultVertexFormats.POSITION);
        wr.pos(iX, iY, aZ).endVertex();
        wr.pos(aX, iY, aZ).endVertex();
        wr.pos(aX, aY, aZ).endVertex();
        wr.pos(iX, aY, aZ).endVertex();
        ts.draw();
        wr.begin(7, DefaultVertexFormats.POSITION);
        wr.pos(aX, iY, aZ).endVertex();
        wr.pos(aX, iY, iZ).endVertex();
        wr.pos(aX, aY, iZ).endVertex();
        wr.pos(aX, aY, aZ).endVertex();
        ts.draw();
    }
    
    /**
     * Renders a block-overlay at the given axis
     * @param block the axis
     */
    public void renderBlockOverlay(AxisAlignedBB block) {
    	Tessellator ts = Tessellator.getInstance();
        BufferBuilder wr = ts.getBuffer();
        wr.begin(7, DefaultVertexFormats.POSITION);
        wr.pos(block.minX, block.minY, block.minZ).endVertex();
        wr.pos(block.minX, block.maxY, block.minZ).endVertex();
        wr.pos(block.maxX, block.maxY, block.minZ).endVertex();
        wr.pos(block.maxX, block.minY, block.minZ).endVertex();
        ts.draw();
        wr.begin(7, DefaultVertexFormats.POSITION);
        wr.pos(block.minX, block.maxY, block.minZ).endVertex();
        wr.pos(block.minX, block.maxY, block.maxZ).endVertex();
        wr.pos(block.maxX, block.maxY, block.maxZ).endVertex();
        wr.pos(block.maxX, block.maxY, block.minZ).endVertex();
        ts.draw();
        wr.begin(7, DefaultVertexFormats.POSITION);
        wr.pos(block.minX, block.minY, block.minZ).endVertex();
        wr.pos(block.minX, block.minY, block.maxZ).endVertex();
        wr.pos(block.minX, block.maxY, block.maxZ).endVertex();
        wr.pos(block.minX, block.maxY, block.minZ).endVertex();
        ts.draw(); 
        wr.begin(7, DefaultVertexFormats.POSITION);
        wr.pos(block.minX, block.minY, block.minZ).endVertex();
        wr.pos(block.maxX, block.minY, block.minZ).endVertex();
        wr.pos(block.maxX, block.minY, block.maxZ).endVertex();
        wr.pos(block.minX, block.minY, block.maxZ).endVertex();
        ts.draw();
        wr.begin(7, DefaultVertexFormats.POSITION);
        wr.pos(block.minX, block.minY, block.maxZ).endVertex();
        wr.pos(block.maxX, block.minY, block.maxZ).endVertex();
        wr.pos(block.maxX, block.maxY, block.maxZ).endVertex();
        wr.pos(block.minX, block.maxY, block.maxZ).endVertex();
        ts.draw();
        wr.begin(7, DefaultVertexFormats.POSITION);
        wr.pos(block.maxX, block.minY, block.maxZ).endVertex();
        wr.pos(block.maxX, block.minY, block.minZ).endVertex();
        wr.pos(block.maxX, block.maxY, block.minZ).endVertex();
        wr.pos(block.maxX, block.maxY, block.maxZ).endVertex();
        ts.draw();
    }
    
    /**
     * Renders the full given image at the position
     * @param image the image's resource location
     * @param x start x
     * @param y start y
     * @param width the width
     * @param height the height
     */
    public void renderImage(MatrixStack ms,ResourceLocation image,int x,int y,int width,int height) {
    	//Sets the texture
		this.mc.getTextureManager().bindTexture(image);
		
		//Prepares the image
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		
		//Renders the image
		AbstractGui.blit(ms,x, y, 0, 0, 0, width, height,width,height);
    }
}

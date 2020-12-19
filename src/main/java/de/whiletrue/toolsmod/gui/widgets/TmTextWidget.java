package de.whiletrue.toolsmod.gui.widgets;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.widgets.preset.TmWidget;
import de.whiletrue.toolsmod.util.TextAlign;
import de.whiletrue.toolsmod.util.classes.TextUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.client.renderer.Tessellator;

public class TmTextWidget extends TmWidget {

	// Position of the widget
	protected int x, y;

	// Color
	protected int color;
	// Scale
	protected double scale = 1;

	// Text align
	private TextAlign alignX = TextAlign.MIDDLE, alignY = TextAlign.BEFORE;

	// Text's to render
	private String[] renderText;

	// If the text should be rendered as high priority
	private boolean specialRender = false;

	public TmTextWidget(int x, int y, String msg, int color) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.setText(msg);
	}

	public TmTextWidget setTextByKey(String key, Object... args) {
		return this.setText(TextUtil.getInstance().getByKey(key, args).split("\n"));
	}

	public TmTextWidget setText(String... msg) {
		this.renderText = msg;
		return this;
	}

	public TmTextWidget setAlignX(TextAlign align) {
		this.alignX = align;
		return this;
	}

	public TmTextWidget setAlignY(TextAlign align) {
		this.alignY = align;
		return this;
	}

	public TmTextWidget setScale(double scale) {
		this.scale = scale;
		return this;
	}

	public TmTextWidget setSpecialRender(boolean special) {
		this.specialRender = special;
		return this;
	}

	@Override
	public boolean isMouseOver(double mX, double mY) {
		return false;
	}

	@Override
	public void move(float... size) {
		this.x = (int) size[0];
		this.y = (int) size[1];
	}

	@Override
	public void onRender(MatrixStack ms,int mX, int mY, float ticks, TmWidget focused) {
		if (this.renderText == null)
			return;

		// Font-renderer
		FontRenderer font = this.mc.fontRenderer;

		// Opens the scale matrix
		GL11.glPushMatrix();
		{
			GL11.glScaled(this.scale, this.scale, this.scale);

			//Gets the full text height
			int textHeight = (int) (font.FONT_HEIGHT * this.renderText.length);
			
			// Renders all strings
			for (int i = 0; i < this.renderText.length; i++) {	
				// Gets the text width
				int textWidth = font.getStringWidth(this.renderText[i]);

				//Calculates the x position for the text
				int x = (int) (this.x/this.scale - (this.alignX.equals(TextAlign.BEFORE) ? 0 :
					this.alignX.equals(TextAlign.MIDDLE) ? textWidth / 2 :
						textWidth));
				
				//Calculates the y position for the text
				int y = (int) (this.y/this.scale + i*font.FONT_HEIGHT - (this.alignY.equals(TextAlign.BEFORE) ? 0
						: this.alignY.equals(TextAlign.MIDDLE) ? textHeight / 2 : textHeight));

				// Renders the text
				if (this.specialRender)
					this.specialRender(font, x, y, i);
				else
					this.mc.fontRenderer.drawStringWithShadow(ms,this.renderText[i], x, y, this.color);
			}
		}
		GL11.glPopMatrix();
	}

	/**
	 * Renders the string with the special attribute
	 * 
	 * Can be used if the string should be displayed in front of items
	 * 
	 * Code from: Screen#renderTooltip
	 */
	private void specialRender(FontRenderer font, int x, int y, int i) {
		this.mc.getItemRenderer().zLevel = 300.0F;
		MatrixStack matrixstack = new MatrixStack();
		IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer
				.getImpl(Tessellator.getInstance().getBuffer());
		matrixstack.translate(0.0D, 0.0D, (double) this.mc.getItemRenderer().zLevel);
		Matrix4f matrix4f = matrixstack.getLast().getMatrix();
		font.renderString(this.renderText[i], (float) x, (float) y, this.color, true, matrix4f, irendertypebuffer$impl,
				false, 0, 15728880);
		irendertypebuffer$impl.finish();
		this.mc.getItemRenderer().zLevel = 0.0F;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
}

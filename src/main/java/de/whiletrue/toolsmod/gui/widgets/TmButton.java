package de.whiletrue.toolsmod.gui.widgets;

import java.util.Optional;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import de.whiletrue.toolsmod.gui.widgets.preset.TmSizeWidget;
import de.whiletrue.toolsmod.gui.widgets.preset.TmWidget;
import de.whiletrue.toolsmod.util.classes.TextUtil;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;

public class TmButton extends TmSizeWidget {

	// Click handler
	protected IPressable onPress;

	// Current displayed string
	protected String display;
	
	// Current displayed tooltip
	protected ITextComponent tooltip;

	// If the button is enabled
	protected boolean enabled = true;

	//Style settings
	private int displayColor = 0xffFFFFFF;
	
	public TmButton(int x, int y, int width, int height, String display, IPressable onPress) {
		super(x, y, width, height);
		this.onPress = onPress;
		this.display = display;
	}

	public TmButton setTooltipp(ITextComponent tooltip) {
		this.tooltip=tooltip;
		return this;
	}

	public TmButton setTooltippByKey(String key,Object... args) {
		this.tooltip=TextUtil.getInstance().getITextByKey(key,args);
		return this;
	}
	public TmButton setOnPress(IPressable onPress) {
		this.onPress = onPress;
		return this;
	}
	public TmButton setEnabled(boolean enabled) {
		this.enabled=enabled;
		return this;
	}
	public TmButton setDisplayByKey(String key,Object... args) {
		this.display=TextUtil.getInstance().getByKey(key,args);
		return this;
	}
	public TmButton setDisplayColor(int displayColor) {
		this.displayColor = (0xff<<24) | displayColor;
		return this;
	}
	
	@Override
	public Optional<TmWidget> onMouseClicked(double mX, double mY, int ticks) {
		if (this.enabled && this.hovered) {
			// Plays the press sound
			this.mc.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1));
			// Executes the press method
			this.onPress.execute(this);
		}
		return Optional.empty();
	}

	@Override
	public void onRender(MatrixStack ms,int mX, int mY, float ticks,TmWidget focused) {
		super.onRender(ms,mX, mY, ticks,focused);
		this.renderButton(ms,focused==this);
	}
	
	@Override
	public boolean onPostRender(MatrixStack ms,int mX, int mY) {
		if(this.hovered&&this.enabled&&this.tooltip!=null) {
			//Renders the tooltipp
			this.mc.currentScreen.renderTooltip(ms,this.tooltip,mX,mY);
			return true;
		}
		return false;
	}

	/**
	 * Renders the button
	 * Copies from the default button
	 */
	private void renderButton(MatrixStack ms,boolean focused) {
		this.mc.getTextureManager().bindTexture(Widget.WIDGETS_LOCATION);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1);
		int i = this.enabled ? this.hovered ? 2 : 1 : 0;
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		
		AbstractGui.blit(ms,this.x, this.y, 0, 0, 46 + i * 20, this.width / 2, this.height,256,256);
		AbstractGui.blit(ms,this.x + this.width / 2, this.y, 0, 200 - this.width / 2, 46 + i * 20, this.width / 2,
				this.height, 256,256);
		int j = this.enabled ? this.displayColor : 10526880;
		this.renderer.renderCenteredString(ms,this.getRenderText(focused), this.x + this.width / 2,
				this.y + (this.height - 8) / 2, j);
	}
	
	/**
	 * Class intern method to get the string to render
	 * 
	 * @param focused if this object is focused
	 */
	protected String getRenderText(boolean focused) {
		return this.display;
	}

	@FunctionalInterface
	public interface IPressable {
		public void execute(TmButton btn);
	}

}

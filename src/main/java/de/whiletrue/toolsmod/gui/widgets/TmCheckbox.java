package de.whiletrue.toolsmod.gui.widgets;

import java.util.Optional;
import java.util.function.Function;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import de.whiletrue.toolsmod.gui.widgets.preset.TmSizeWidget;
import de.whiletrue.toolsmod.gui.widgets.preset.TmWidget;
import de.whiletrue.toolsmod.util.classes.TextUtil;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class TmCheckbox extends TmSizeWidget{

	//Reference to the texture
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
	
	//If the box is checked
	private boolean checked;
	
	//Toggle event handler
	private Function<Boolean, Boolean> onToggle;
	
	//Tooltip of the box
	private ITextComponent tooltip;
	
	public TmCheckbox(int x, int y,boolean isChecked, Function<Boolean, Boolean> onToggle) {
		super(x, y, 20, 20);
		this.checked=isChecked;
		this.onToggle=onToggle;
	}
	
	@Override
	public void move(float... size) {
		this.x=(int)size[0];
		this.y=(int)size[1];
	}
	
	@Override
	public void onRender(MatrixStack ms,int mX, int mY, float ticks, TmWidget focused) {
		super.onRender(ms,mX, mY, ticks, focused);
		//Renders the checkbox
		this.mc.getTextureManager().bindTexture(TEXTURE);
		RenderSystem.enableDepthTest();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		AbstractGui.blit(ms,this.x, this.y, 0.0F, this.checked ? 20.0F : 0.0F, 20, 20, 64, 64);
	}
	
	@Override
	public Optional<TmWidget> onMouseClicked(double mX, double mY, int ticks) {
		if(this.hovered)
			//Updates the checkbox
			this.checked=this.onToggle.apply(!this.checked);
		return Optional.empty();
	}
	
	@Override
	public boolean onPostRender(MatrixStack ms,int mX, int mY) {
		//Checks if the box is hovered and if the tooltipp is set
		if(this.hovered&&this.tooltip!=null) {
			//Renders the tooltipp
			this.mc.currentScreen.renderTooltip(ms,this.tooltip,mX,mY);
			return true;
		}
		return false;
	}
	
	public TmCheckbox setTooltipp(ITextComponent tooltipp) {
		this.tooltip = tooltipp;
		return this;
	}
	public TmCheckbox setTooltippByKey(String key,Object... args) {
		this.tooltip = TextUtil.getInstance().getITextByKey(key, args);
		return this;
	}
	
	public boolean isChecked() {
		return this.checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

}

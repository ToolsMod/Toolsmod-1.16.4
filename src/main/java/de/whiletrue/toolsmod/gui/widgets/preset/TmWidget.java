package de.whiletrue.toolsmod.gui.widgets.preset;

import java.util.Optional;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.TmScreen;
import de.whiletrue.toolsmod.util.classes.RenderUtil;
import net.minecraft.client.Minecraft;

public abstract class TmWidget {

	//Reference to the game
	protected Minecraft mc = Minecraft.getInstance();
	
	//Reference to the renderer
	protected RenderUtil renderer = RenderUtil.getInstance();

	/**
	 * Unfocuses the current widget
	 */
	protected final void unFocuse() {
		((TmScreen)this.mc.currentScreen).unFocuse();
	}
	
	public abstract boolean isMouseOver(double mX,double mY);
	
	public abstract void move(float... size);

	/**
	 * Method to update properties that have to be set once the widget gets added to the screen
	 */
	public void onInitUpdate() {}
	
	public void onRender(MatrixStack ms,int mX,int mY,float ticks,@Nullable TmWidget focused) {}

	public boolean onPostRender(MatrixStack ms,int mX,int mY) {
		return false;
	}
	
	public Optional<TmWidget> onMouseClicked(double mX,double mY,int ticks) {
		return Optional.ofNullable(this.isMouseOver(mX, mY)?this:null);
	}
	
	public boolean onMouseDragged(double mX, double mY, int arg2, double arg3, double arg4) {
		return false;
	}
	
	public boolean onMouseReleased(double mX, double mY, int ticks) {
		return false;
	}
	
	public boolean onMouseScrolled(double mX,double mY,double strength) {
		return false;
	}

	public boolean onKeyPressed(int keyCode,int scanCode,int modifiers) {
		return false;
	}
	
	public boolean onKeyReleased(int keyCode,int scanCode,int modifiers) {
		return false;
	}
	
	public boolean onCharTyped(char key,int keyCode) {
		return false;
	}
	
	public void onTick() {}
}

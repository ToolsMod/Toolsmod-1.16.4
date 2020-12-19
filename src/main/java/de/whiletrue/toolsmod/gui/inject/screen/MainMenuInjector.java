package de.whiletrue.toolsmod.gui.inject.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.config.ConfigGui;
import de.whiletrue.toolsmod.gui.inject.ScreenInjector;
import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.util.classes.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Pre;

public class MainMenuInjector extends ScreenInjector {
	
	@Override
	public void preInit(Pre evt) {}

	@Override
	public void postInit(Post evt) {
		Screen screen = evt.getGui();
		
		// Adds the toolsmod button
		evt.addWidget(
				new Button(screen.width - 80, 10, 70, 20, new StringTextComponent(Toolsmod.NAME), btn -> ConfigGui.CONFIG_SCREENS.open()));

		// Gets the remote version
		float rmtVer = Toolsmod.getInstance().getUpdater().getRemote();

		// Height calculator for the main menu
		int j = screen.height / 4 + 48;

		// Checks if the remote version failed to load
		if (rmtVer == -1) {
			// Adds the failed info
			evt.addWidget(this.getTextWidget(screen.width / 2, j - 14, true, "failed"));
		} else
		// Checks if the version is under the current version
		if (rmtVer > Toolsmod.MOD_VERSION) {
			// Adds the update button
			evt.addWidget(new Button(screen.width / 2 + 50, j - 25, 50, 20,
					TextUtil.getInstance().getITextByKey("updater.start"), i -> {
						//Opens the download url
						Util.getOSType().openURI("https://toolsmod.github.io/api/downloads/tm-forge-1.15.jar");
					}));

			// Adds the update info
			evt.addWidget(this.getTextWidget(screen.width / 2 - 100, j - 20, false, "available"));
		}
	}
	
	/**
	 * Creates and improvised text widget
	 * 
	 * @param x
	 *            the x position
	 * @param y
	 *            the y position
	 * @param text
	 *            the text
	 */
	public Widget getTextWidget(int x, int y, boolean centered, String text) {
		// Gets the font renderer reference
		FontRenderer font = Minecraft.getInstance().fontRenderer;

		// Gets the text
		ITextComponent value = TextUtil.getInstance().getITextByKey("updater." + text);

		// Checks if the text should be centered
		if (centered)
			x -= font.getStringWidth(value.getString()) / 2;

		return new Widget(x, y, 200, 20, value) {
			@Override
			public void render(MatrixStack ms,int mX, int mY, float ticks) {
				font.drawStringWithShadow(ms,this.getMessage().getString(), this.x, this.y, 0xFFffffff);
			}

			@Override
			public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
				return false;
			}
		};
	}
}

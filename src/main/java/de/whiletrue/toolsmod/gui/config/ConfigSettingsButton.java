package de.whiletrue.toolsmod.gui.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.whiletrue.toolsmod.gui.widgets.TmButton;
import de.whiletrue.toolsmod.gui.widgets.preset.TmWidget;
import de.whiletrue.toolsmod.mod.Toolsmod;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;

public class ConfigSettingsButton extends TmButton{

	//Reference to the settings button
	private static ResourceLocation SETTINGS_IMAGE = new ResourceLocation(Toolsmod.ID,"textures/config/settings_button.png");
	
	public ConfigSettingsButton(int x, int y, IPressable onPress) {
		super(x, y, 20, 20, null, onPress);
	}
	
	@Override
	public void onRender(MatrixStack ms,int mX, int mY, float ticks, TmWidget focused) {
		//Updates the hover state
		this.hovered=this.isMouseOver(mX, mY);
		
		//Renders the button's image
		this.mc.getTextureManager().bindTexture(SETTINGS_IMAGE);
	    RenderSystem.enableBlend();
	    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        
	    //Gets the index of the position of the right image
        int i = this.enabled?this.hovered?2:1:0;
        
        //Renders the image
        AbstractGui.blit(ms,this.x, this.y, this.width, this.height, 0, i*20, this.width, this.height, 20, 60);
	}

}

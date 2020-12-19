package de.whiletrue.toolsmod.gui.config.sub.modules;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.config.ConfigGui;
import de.whiletrue.toolsmod.gui.modules.GuiQAModules;
import de.whiletrue.toolsmod.gui.quickaccess.GuiQuickAccess;
import de.whiletrue.toolsmod.gui.widgets.TmBackgroundWidget;
import de.whiletrue.toolsmod.gui.widgets.TmButton;
import de.whiletrue.toolsmod.gui.widgets.TmTextWidget;
import de.whiletrue.toolsmod.gui.widgets.rounding.listscaleable.ScaleableListView;
import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.settings.views.SettingView;
import de.whiletrue.toolsmod.util.TextAlign;

public class GuiDefaultSettings extends GuiQuickAccess{

	//If the gui got open ingame or in the main menu
	private boolean isIngame;
	
	//Reference to the module
	private Module mod;
	
	//List with all settings
	private ScaleableListView<SettingView<?>> list = new ScaleableListView<SettingView<?>>(0,0,0,0)
			.setSpaceY(10)
			.setScrollStrength(20);
	
	public GuiDefaultSettings(Module mod) {
		super('#'+mod.getName(),"gui.config.modules.settings",320, 190);
		this.mod=mod;
	}
	
	@Override
	protected void init() {
		//If the gui got open ingame or in the main menu
		this.isIngame = this.minecraft.world!=null;
		
		if(this.isIngame) {
			
			//Appends the ingame list
			this.addWidgetWithListener(this.list, (x,y)->new float[] {
					this.width/2-x/2+50+20,
					this.height/2-y/2+25,
					x-50-40,
					y-50
			});
			
			//Appends the close button
			this.addWidgetWithListener(new TmButton(0, 0, 0, 0, "", i->this.closeScreen())
					.setDisplayByKey("settings.back"), (x,y)->new float[] {
				this.width/2,
				this.height/2+y/2-45,
				50,
				20
			});
		}else{
			//Appends the upper view
			this.addWidget(new TmTextWidget(this.width/2, 20, this.mod.getName(), 0xFFffffff)
					.setScale(2)
					.setAlignY(TextAlign.MIDDLE));
			
			//Appends the dark background render
			this.addWidget(new TmBackgroundWidget(0,40,this.width, this.height-40, 0xa0000000));

			//Appends the close button
			this.addWidget(new TmButton(this.width-55, 10, 50, 20,null, i->this.closeScreen())
					.setDisplayByKey("settings.back"));
			
			//Move the list to the position
			this.list.move(this.width*.25f,50,this.width*.5f,this.height-50);
			//Appends the menu list
			this.addWidget(this.list);
		}
		
		//Sets the items
		this.list.setItems(
			//Gets all settings
			this.mod.getSettings().stream()
			.filter(i->!i.isInvisible())//Filters the invisible ones
			.map(i->i.getView(this.mod))//Gets their views
			.toArray(SettingView<?>[]::new)//Gets them
		);
	}

	@Override
	public void render(MatrixStack ms,int mouseX, int mouseY, float p_render_3_) {
		if(!this.isIngame)
			this.renderDirtBackground(0);
		
		super.render(ms,mouseX, mouseY, p_render_3_);
	}
	
	@Override
	protected boolean mouseClickedQuickAccessQui(double mX, double mY) {
		return this.isIngame ? super.mouseClickedQuickAccessQui(mX, mY) : false;
	}
	
	@Override
	protected void renderQuickAccessGui(MatrixStack ms) {
		if(this.isIngame)
			super.renderQuickAccessGui(ms);
	}
	
	@Override
	public void closeScreen() {
		
		if(this.isIngame) {			
			//Saves the settings
			Toolsmod.getInstance().getModuleManager().save();
			//Opens the module gui
			GuiQAModules.MODULES_GUIS.open();
		}else
			//Opens the old settings
			ConfigGui.CONFIG_SCREENS.open();
	}
}

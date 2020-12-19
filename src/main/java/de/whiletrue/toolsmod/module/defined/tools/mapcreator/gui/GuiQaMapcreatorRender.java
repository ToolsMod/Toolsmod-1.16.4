package de.whiletrue.toolsmod.module.defined.tools.mapcreator.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.quickaccess.GuiQuickAccess;
import de.whiletrue.toolsmod.gui.widgets.TmTextWidget;
import de.whiletrue.toolsmod.gui.widgets.TmUpdateButton;
import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.module.defined.tools.ModuleMapcreator;

public class GuiQaMapcreatorRender extends GuiQuickAccess{

	//Reference to the module
	private ModuleMapcreator module;
	
	//Info which render setting is choosen
	private TmTextWidget renderInfo = new TmTextWidget(0, 0, null, 0xFFffffff);
	
	public GuiQaMapcreatorRender() {
		super("modules.mapcreator.gui.render.name", "modules.mapcreator.gui.render.name.short", 340,200);
	}
	
	@Override
	protected void init() {
		//Gets the module reference
		this.module= Toolsmod.getInstance().getModuleManager().getModuleByClass(ModuleMapcreator.class).get();
		
		//Adds the rendering text
		this.addWidgetWithListener(this.renderInfo, (x,y)->new float[] {
			this.width/2+25,
			this.height/2
		});
		this.renderInfo.setText(this.module.getRendering().getName());
		
		//Adds the rendering-info text
		this.addWidgetWithListener(new TmTextWidget(0, 0, "", 0xFFffffff)
				.setTextByKey("modules.mapcreator.gui.render.info"), (x,y)->new float[] {
			this.width/2+25,
			this.height/2-20
		});
		
		//Adds the switch buttons
		this.addWidgetWithListener(new TmUpdateButton(0, 0, 0, 0, btn->{
			if(btn!=null)
				this.renderInfo.setText(
					this.module.switchRendering(true).getName()
				);
			return "<";
		}),(x,y)->new float[] {
				this.width/2-25,
				this.height/2-5,
				12,
				20
		});
		//Adds the switch buttons
		this.addWidgetWithListener(new TmUpdateButton(0, 0, 0, 0, btn->{
			if(btn!=null)
				this.renderInfo.setText(
					this.module.switchRendering(false).getName()
				);
			return ">";
		}),(x,y)->new float[] {
				this.width/2+65,
				this.height/2-5,
				12,
				20
		});
	}
	
	@Override
	public void render(MatrixStack ms,int mouseX, int mouseY, float p_render_3_) {
		super.render(ms,mouseX, mouseY, p_render_3_);
		//Render-info background
		this.renderer.renderRect(
			ms,
			this.width/2-13,
			this.height/2-5,
			78,
			20,
			0x60000000
		);
		this.renderInfo.onRender(ms,mouseX, mouseY, p_render_3_,null);
	}
}

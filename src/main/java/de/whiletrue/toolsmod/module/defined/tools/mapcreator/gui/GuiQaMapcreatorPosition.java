package de.whiletrue.toolsmod.module.defined.tools.mapcreator.gui;

import de.whiletrue.toolsmod.gui.quickaccess.GuiQuickAccess;
import de.whiletrue.toolsmod.gui.widgets.TmUpdateButton;
import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.module.defined.tools.ModuleMapcreator;
import de.whiletrue.toolsmod.util.classes.TextUtil;
import net.minecraft.util.math.BlockPos;

public class GuiQaMapcreatorPosition extends GuiQuickAccess{

	//Reference to the module
	private ModuleMapcreator module;
	
	public GuiQaMapcreatorPosition() {
		super("modules.mapcreator.gui.pos.name","modules.mapcreator.gui.pos.name.short",340,200);
	}

	@Override
	protected void init() {
		//Loads the module
		this.module=(ModuleMapcreator) Toolsmod.getInstance().getModuleManager().getModuleByClass(ModuleMapcreator.class).get();

		//Adds the position button
		this.addWidget(new TmUpdateButton(this.width/2-15, this.height/2-40, 80, 20, btn->{
			if(btn!=null)
				//Updates the position
				this.module.setPosition(this.minecraft.player.getPosition());
				
			return TextUtil.getInstance().getByKey("modules.mapcreator.gui.pos.position");
		})
		.setTooltippByKey("modules.mapcreator.gui.pos.player"));
		

		//Adds the chunk button
		this.addWidget(new TmUpdateButton(this.width/2-15, this.height/2, 80, 20, btn->{
			if(btn!=null) {
				//Gets the chunk position
				BlockPos set = new BlockPos(
					this.minecraft.player.chunkCoordX*16,
					(int)this.minecraft.player.getPosY(),
					this.minecraft.player.chunkCoordZ*16
				);
				
				//Sets the position
				this.module.setPosition(set);
			}
			return TextUtil.getInstance().getByKey("modules.mapcreator.gui.pos.chunk");
		}).setTooltippByKey("modules.mapcreator.gui.pos.chunk"));
	}
	
}

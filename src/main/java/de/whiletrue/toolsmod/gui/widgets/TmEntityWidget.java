package de.whiletrue.toolsmod.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.widgets.preset.TmSizeWidget;
import de.whiletrue.toolsmod.gui.widgets.preset.TmWidget;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.LivingEntity;

public class TmEntityWidget extends TmSizeWidget{

	//The entity to render
	private LivingEntity entity;
	
	//Size of the entity
	private int size;
	
	public TmEntityWidget(int x, int y,int watchX,int watchY,int size,LivingEntity entity) {
		super(x, y, watchX,watchY);
		this.entity=entity;
		this.size=size;
	}
	
	@Override
	public void onRender(MatrixStack ms,int mX, int mY, float ticks, TmWidget focused) {
		//Removes the custom name
		boolean vis = this.entity.isCustomNameVisible();
		this.entity.setCustomNameVisible(false);
		
		//Renders the entity
		InventoryScreen.drawEntityOnScreen(this.x, this.y,this.size, this.x-this.width, this.y-this.height, this.entity);
		
		//Re-appends the custom name
		this.entity.setCustomNameVisible(vis);
	}
}
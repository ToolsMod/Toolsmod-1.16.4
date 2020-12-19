package de.whiletrue.toolsmod.module.defined.tools.asedit.gui.list;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.widgets.TmButton;
import de.whiletrue.toolsmod.gui.widgets.TmCheckboxColor;
import de.whiletrue.toolsmod.gui.widgets.TmEntityWidget;
import de.whiletrue.toolsmod.gui.widgets.TmTextfield;
import de.whiletrue.toolsmod.gui.widgets.preset.TmWidget;
import de.whiletrue.toolsmod.gui.widgets.rounding.listmultirow.MultirowListItem;
import de.whiletrue.toolsmod.module.defined.tools.asedit.EditableArmorStand;

public class ASEditItem extends MultirowListItem<EditableArmorStand>{

	//Description-field
	private TmTextfield descField;
	
	//Defines if the stand is selected
	private TmCheckboxColor selectedBox;
	
	//Up and down button
	private TmButton upBtn,downBtn;
	
	//Entity renderer
	private TmEntityWidget entityRenderer;
	
	public ASEditItem(EditableArmorStand item,boolean extended,Consumer<Integer> onSelect,BiConsumer<Integer, Boolean> onMove) {
		super(item);

		//Creates the description field
		this.widgets.add(this.descField = new TmTextfield(0, 0, 0, 0, item.getReferenceName(), item::setReferenceName));
		
		//Creates the selection box
		this.widgets.add(this.selectedBox=new TmCheckboxColor(0, 0, 0, 0, item.isSelected(), c->{
			onSelect.accept(this.index);
			return true;
		}));
		
		//Checks if the items are extended
		if(extended) {			
			//Adds the move up and down button
			this.widgets.add(this.upBtn=new TmButton(0, 0, 0, 0, "/\\", btn->{
				onMove.accept(this.index,false);
			}));
			this.widgets.add(this.downBtn=new TmButton(0, 0, 0, 0, "\\/", btn->{
				onMove.accept(this.index,true);
			}));
			
			//Adds the entity widget
			this.widgets.add(this.entityRenderer=new TmEntityWidget(0, 0, 0, 0, 15, item));
		}
	}
	
	@Override
	public void handlChangePosition(int x, int y, int w, int h) {
		super.handlChangePosition(x, y, w, h);
		
		//Checks if the view is extended
		boolean extended = this.upBtn!=null;
		
		//Adjusts the widgets
		this.descField.move(x+(extended?30:5),y+5,w-(extended?50:25),10);
		this.selectedBox.move(x+w-15,y+5,10,10);
				
		//Checks if the items are extended
		if(extended) {			
		
			//Calculates the space between the up/down button to space-around them
			int space = (w-50-40)/3;
			
			//Updates the move buttons
			this.upBtn.move(x+30+space,y+h-25,20,20);
			this.downBtn.move(x+30+space*2+20,y+h-25,20,20);
			
			//Updates the entity renderer
			this.entityRenderer.move(x+15,y+height-8,x+400,y+400);
		}
	}

	@Override
	public void handleListSizeChange(int size) {
		//Checks if the items are extended
		if(this.upBtn!=null) {
			//Updates the buttons
			this.upBtn.setEnabled(this.index>0);
			this.downBtn.setEnabled(this.index<size-1);
		}
	}
	
	@Override
	public void render(MatrixStack ms,int mX, int mY, float ticks, TmWidget focused) {
		RENDERER.renderRect(ms,this.x, this.y, this.width, this.height, this.selectedBox.isChecked()?0xff044A8A:0xff474747);
		super.render(ms,mX, mY, ticks, focused);
	}
	
	/**
	 * Updates if the item is selected
	 * @param select if the item is selected
	 */
	public void doSelect(boolean select) {
		this.item.setSelected(select);
		this.selectedBox.setChecked(select);
	}
	
}

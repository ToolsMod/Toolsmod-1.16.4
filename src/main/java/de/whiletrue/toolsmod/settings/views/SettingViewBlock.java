package de.whiletrue.toolsmod.settings.views;

import de.whiletrue.toolsmod.gui.widgets.TmTextfield;
import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.settings.defined.SettingBlock;

public class SettingViewBlock extends SettingView<SettingBlock>{

	//Gui-widget
	private TmTextfield sField = (TmTextfield) new TmTextfield()
			.setValidator(i->i.isEmpty() || this.item.handleParse(i))//Checks if the item is valid and updates it
			.setText(this.item.handleSave())
			.setHeight(15);//Sets the height
	
	public SettingViewBlock(SettingBlock item, Module mod) {
		super(item, mod);
		
		//Appends the widget
		this.widgets.add(this.sField);
	}
	
	@Override
	public int handleChangePosition(int x, int w) {
		//Positions the field
		this.sField.setX(x).setWidth(w);
		
		return super.handleChangePosition(x, w)+17;
	}
	
	@Override
	public void handleChangePositionScrolled(int y) {
		//Scrolls the field
		this.sField.setY(y+11);
		
		super.handleChangePositionScrolled(y);
	}
	
	

}

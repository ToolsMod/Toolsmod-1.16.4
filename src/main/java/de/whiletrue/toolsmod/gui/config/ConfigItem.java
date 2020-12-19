package de.whiletrue.toolsmod.gui.config;

import de.whiletrue.toolsmod.gui.widgets.TmBackgroundWidget;
import de.whiletrue.toolsmod.gui.widgets.TmTextfield;
import de.whiletrue.toolsmod.gui.widgets.rounding.listmultirow.MultirowListItem;

public class ConfigItem extends MultirowListItem<String>{

	public ConfigItem(String item) {
		super(item);
		
		this.widgets.add(new TmBackgroundWidget(0,0,0,0, 0x90ffff00));
		this.widgets.add(new TmTextfield(0, 0, 0, 0, item,null));
	}
	
	@Override
	public void handlChangePosition(int x, int y, int w, int h) {
		this.widgets.get(0).move(x,y,w,h);
		this.widgets.get(1).move(x+w/2+10,y,80,20);
	}
}

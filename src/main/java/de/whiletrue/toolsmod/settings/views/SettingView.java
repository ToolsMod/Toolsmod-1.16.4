package de.whiletrue.toolsmod.settings.views;

import javax.annotation.Nullable;

import de.whiletrue.toolsmod.gui.widgets.TmTextWidget;
import de.whiletrue.toolsmod.gui.widgets.rounding.listscaleable.ScaleableListItem;
import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.settings.Setting;
import de.whiletrue.toolsmod.util.TextAlign;
import de.whiletrue.toolsmod.util.classes.TextUtil;

public abstract class SettingView<T extends Setting<?>> extends ScaleableListItem<T>{

	// Title widget
	private TmTextWidget title;

	//Title string
	private String titleString;
	
	public SettingView(T item,@Nullable Module mod) {
		super(item);
		
		//Gets the title
		this.titleString = TextUtil.getInstance().getByKey(				
			//Checks if the mod is given or if the setting is global
			mod != null ?
			//Gets the module setting
			"settings."+mod.getName().toLowerCase()+'.'+item.getName() :
			//Gets the global setting
			"global.settings."+item.getName()
		);
		
		// Creates the title
		this.widgets.add(this.title = new TmTextWidget(0, 0, "", 0xFFffffff)
				.setAlignX(TextAlign.BEFORE)
				.setText(this.titleString));
	}
	
	@Override
	public int handleChangePosition(int x, int w) {
		super.handleChangePosition(x, w);
		//Updates the title
		this.title.setX(x);
		return 10;
	}
	
	@Override
	public void handleChangePositionScrolled(int y) {
		super.handleChangePositionScrolled(y);
		
		//Updates the title
		this.title.setY(y);
	}
	
	/**
	 * Checks if this view is valid with the given search
	 */
	public boolean isValid(String search) {
		return this.titleString.toLowerCase().contains(search.toLowerCase());
	}
}

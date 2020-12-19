package de.whiletrue.toolsmod.gui.modules;

import java.util.Arrays;

import de.whiletrue.toolsmod.gui.GuiGroup;
import de.whiletrue.toolsmod.gui.quickaccess.GuiQuickAccess;
import de.whiletrue.toolsmod.gui.widgets.rounding.listmultirow.MultirowListView;
import de.whiletrue.toolsmod.mod.ModSettings;
import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.module.ModuleCategory;

public class GuiQAModules extends GuiQuickAccess{
	
	//All module guis
	public final static GuiGroup<GuiQAModules> MODULES_GUIS = GuiGroup.of(
		//Gets all modules
		Arrays.stream(ModuleCategory.values()).map(GuiQAModules::new).toArray(GuiQAModules[]::new)
	);
	
	//List with the moduels
	private final MultirowListView<ModuleItem> list = new MultirowListView<ModuleItem>(0,0,0,0)
			.setListFormatting(5, 5, 4, 20)
			.setScrollStrength(4);
	
	//Module category
	private ModuleCategory category;
	
	protected GuiQAModules(ModuleCategory category) {
		super("module.category."+category.getKey(), "module.category."+category.getKey(), 400 ,250);
		this.category=category;
	}
	
	@Override
	protected void init() {
		super.init();
		
		//Adds the list
		this.addWidget(this.list);
		
		//Adds all modules
		this.list.setItems(
			Toolsmod.getInstance().getModuleManager().getModulesByCategory(this.category)
			.stream()
			.filter(i->i.isAllowed())
			.map(ModuleItem::new)
			.toArray(ModuleItem[]::new)
		);
	}
	
	@Override
	protected void handleSizeChange(int sizeX, int sizeY) {
		this.list.move(this.width/2-sizeX/2+55,this.height/2-sizeY/2+25,sizeX-60,sizeY-30);
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

		//Checks if the qa gui key got pressed
		if(ModSettings.quickAccessKeybind.value.isKeycodeMatching(keyCode)) {
			//Closes the gui
			this.onClose();
			return true;
		}
		
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	
}
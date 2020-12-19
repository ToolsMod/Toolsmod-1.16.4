package de.whiletrue.toolsmod.module.defined.tools.asedit.gui;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.TmScreen;
import de.whiletrue.toolsmod.gui.widgets.TmBackgroundWidget;
import de.whiletrue.toolsmod.gui.widgets.TmItemButton;
import de.whiletrue.toolsmod.gui.widgets.rounding.EnumListSlider;
import de.whiletrue.toolsmod.gui.widgets.rounding.listmultirow.MultirowListView;
import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.module.defined.tools.ModuleAsEdit;
import de.whiletrue.toolsmod.module.defined.tools.asedit.AsEditManager;
import de.whiletrue.toolsmod.module.defined.tools.asedit.gui.list.ASEditItem;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.Items;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class GuiAsEdit extends TmScreen{

	//List with all stands
	private static MultirowListView<ASEditItem> LIST = new MultirowListView<ASEditItem>(0,0,0,0)
			.setBackground(0x9f000000)
			.setScrollStrength(8)
			.setSlider(EnumListSlider.LEFT);
	
	//Reference to the module
	protected ModuleAsEdit module;
	
	//Reference to the manager
	protected AsEditManager manager;
	
	//Available screen between the overlay and the normal gui
	protected int overlayX=50,overlayWidth;
	
	//Last selected id for multi selection
	private static int LAST_SELECTED = -1;
	
	//If the items should be extended
	private boolean extendItems;
	
	public GuiAsEdit(String nameKey) {
		super(new TranslationTextComponent(Toolsmod.ID+".modules.asedit.gui."+nameKey));
	}

	@Override
	protected void init() {
		//Gets the module
		this.module = Toolsmod.getInstance().getModuleManager().getModuleByClass(ModuleAsEdit.class).get();
		
		//Gets the manager
		this.manager=this.module.getManager();
		
		//Checks if the items should be extended
		this.extendItems=this.module.sExtendedList.value;
		
		//Sets the overlay
		this.overlayWidth=this.width-this.overlayX;
	}
	
	@Override
	public void render(MatrixStack ms,int mX, int mY, float ticks) {
		super.render(ms,mX, mY, ticks);
		
		//Renders the gui-selection background
		fill(ms,0, 0, this.overlayX, this.height, 0x9f000000);
		
		//Renders the gui-selection buttons
		for(int i=0;i<this.module.guis.getSize();i++) {
			//Reference to the gui
			GuiAsEdit gui = this.module.guis.getGui(i);

			//Calculates the y position
			int y = this.height/2+i*20-this.module.guis.getSize()*10;
			
			//Renders the field
			fill(ms,0,y,this.overlayX,y+20,this.module.guis.getSelected().equals(gui)?0xffEE5A24:0xFF000000);
			
			//Renders the name
			drawCenteredString(ms,this.minecraft.fontRenderer, gui.getTitle().getString(), this.overlayX/2, y+6, 0xFFffffff);
		}
	}
	
	@Override
	public boolean mouseClicked(double mX, double mY, int ticks) {
		
		//Checks if any button for the gui-selection got clicked
		for(int i=0;i<this.module.guis.getSize();i++) {
			//Calculates the y position
			int y = this.height/2+i*20-this.module.guis.getSize()*10;
			
			if(mX>0&&mX<this.overlayX && mY>y&&mY<y+20) {
				this.module.guis.update(i);
				return true;
			}
		}
		
		return super.mouseClicked(mX, mY, ticks);
	}
	
	/**
	 * Appends the stand list to the gui
	 */
	protected void appendList() {
		//Calculates the width of the list
		int lw = (int)Math.max(this.width*.25f, 120);
		
		//Updates the gui width
		this.overlayWidth-=lw;

		//Button width
		int btnW = 20;
		
		//Calculates the space between the buttons
		int space = (lw-btnW*4)/5;
		
		//Calculates the start x position
		int x = this.width-lw;
		
		//Moves the list
		LIST.move(x, 40, lw, this.height-40);
		//Updates the lists formatting
		LIST.setListFormatting(0, 0, 1, this.extendItems?45:20);
		//Adds the stands
		this.resetStands();
		//Adds the list
		this.addWidget(LIST);
		
		//Creates the controller background
		this.addWidget(new TmBackgroundWidget(x-6, 0, lw+6, 40, 0xff00E2FF).setOutline(0xff000000,1));
		
		//Adds the delete stand button
		this.addWidget(new TmItemButton(x+space,10,Items.BARRIER, btn->{
			//Deletes all stands and sets the last last selected index
			LAST_SELECTED=this.manager.doDelete();
			//Updates the gui
			this.module.guis.update();
		}).setTooltippByKey("modules.asedit.gui.global.button.delete"));
		
		//Adds the clone button
		this.addWidget(new TmItemButton(x+btnW+space*2,10,Items.PAPER, btn->{
			//Clones the stand and sets the new last selected
			LAST_SELECTED=this.manager.doClone();
			//Updates the list
			this.resetStands();
		}).setTooltippByKey("modules.asedit.gui.global.button.clone"));

		//Adds the new stand button
		this.addWidget(new TmItemButton(x+btnW*2+space*3,10,Items.ARMOR_STAND, btn->{
			//Adds the stand
			this.manager.addNewStand(this.minecraft.player.getPosition(),null);
			//Selects the new stand
			this.handleSelect(this.manager.getStands().size()-1);
		}).setTooltippByKey("modules.asedit.gui.global.button.new"));
		
		//Adds the teleport button
		this.addWidget(new TmItemButton(x+btnW*3+space*4,10,Items.ENDER_PEARL, btn->this.handleTeleport()).setTooltippByKey("modules.asedit.gui.global.button.tp"));
	}
	
	/**
	 * Removes and readds all stands to the list
	 */
	private void resetStands() {
		//Sets the stands
		LIST.setItems(this.manager.getStands().stream().map(s->new ASEditItem(s,this.extendItems, this::handleSelect, (index,up)->{
			//Moves the current item up/down
			this.manager.doMoveList(index, up);
			//Resets the stands
			this.resetStands();
		})).toArray(ASEditItem[]::new));
		
	}
	
	/**
	 * Handler for the select stand event
	 * @param index the index of the stand
	 */
	private void handleSelect(int index) {
		//If the shift key is pressed
		boolean shiftDown = InputMappings.isKeyDown(this.minecraft.getMainWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT);

		//If the ctrl-key is pressed
		boolean ctrlDown = InputMappings.isKeyDown(this.minecraft.getMainWindow().getHandle(), GLFW.GLFW_KEY_LEFT_CONTROL);
		
		//Checks if the key is down
		if(shiftDown) {
			//Gets the upper and lower id
			int up = Math.max(index, LAST_SELECTED);
			int low = Math.min(index, LAST_SELECTED);
			
			//Sets the key and removes all others
			for(int i=0;i<LIST.getViews().size(); i++)
				LIST.getViews().get(i).doSelect(i>=low&&i<=up);
			
			//Updates the selection
			this.manager.handleSelect(-1);
		}else if(ctrlDown){
			//Gets the item
			ASEditItem itm = LIST.getViews().get(index);

			//Checks if the item already was selected
			if(!itm.getItem().isSelected()) {				
				itm.doSelect(true);
				//Updates the last selected
				LAST_SELECTED=index;
				
				//Updates the selection
				this.manager.handleSelect(-1);
			}
		}else {
			//Sets the key and removes all others
			for(int i=0;i<LIST.getViews().size(); i++)
				LIST.getViews().get(i).doSelect(index==i);
			
			//Updates the last selected
			LAST_SELECTED=index;

			//Updates the selection
			this.manager.handleSelect(index);
		}
		
		//Updates the gui
		this.module.guis.update();
	}
	
	/**
	 * Handles the click on the teleport to me button
	 */
	protected void handleTeleport() {
		//Teleports all selected stands to the player
		this.manager.doTeleportToPlayer();
	}
}

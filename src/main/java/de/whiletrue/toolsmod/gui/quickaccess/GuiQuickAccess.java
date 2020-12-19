package de.whiletrue.toolsmod.gui.quickaccess;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.GuiGroup;
import de.whiletrue.toolsmod.gui.TmScreen;
import de.whiletrue.toolsmod.gui.widgets.preset.TmWidget;
import de.whiletrue.toolsmod.mod.ModSettings;
import de.whiletrue.toolsmod.util.classes.RenderUtil;
import de.whiletrue.toolsmod.util.classes.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;

public class GuiQuickAccess extends TmScreen {

	// Current x and y size for the GUI
	private static int currentX = 200, currentY = 200;
	// Requested x and y (Fixes size of the qa gui)
	private final int x, y;

	// Title of the gui (Language Keys)
	private String guiTitleKey, guiTitleShortKey;

	// Actual gui titles
	private String guiTitle, guiTitleShort;

	// Reference to the group
	protected GuiGroup<GuiQuickAccess> group;

	// Reference to the renderer
	protected RenderUtil renderer = RenderUtil.getInstance();

	// Resizeable component listeners
	protected Map<TmWidget, BiFunction<Integer, Integer, float[]>> sizeChangeListeners = new HashMap<>();
	
	protected GuiQuickAccess(String titleKey, String shortTitleKey, int x, int y) {
		this.x = x;
		this.y = y;
		this.guiTitleKey = titleKey;
		this.guiTitleShortKey = shortTitleKey;
	}

	@Override
	public void init(Minecraft p_init_1_, int p_init_2_, int p_init_3_) {
		//Resets all size listeners
		this.sizeChangeListeners.clear();
		super.init(p_init_1_, p_init_2_, p_init_3_);
		
		// Sends the first update
		this.handleSizeChange(currentX, currentY);
	}

	@SuppressWarnings("unchecked") 
	@Override
	public void onGroupGuiUpdate(GuiGroup<? extends TmScreen> group) {
		//Checks if the title is a key or default
		this.guiTitle = this.guiTitleKey.charAt(0) == '#' ?
				//Gets the key as a normal name
				this.guiTitleKey.substring(1) :
				//Gets the key as a language key
				TextUtil.getInstance().getByKey(this.guiTitleKey);
		
		this.guiTitleShort = this.guiTitleShortKey.charAt(0) == '#' ?
				//Gets the key as a normal name
				this.guiTitleShortKey.substring(1) :
				//Gets the key as a language key
				TextUtil.getInstance().getByKey(this.guiTitleShortKey);
		
		//Updates the group
		this.group=(GuiGroup<GuiQuickAccess>) group;
	}

	@Override
	public void render(MatrixStack ms,int mouseX, int mouseY, float p_render_3_) {
		this.renderQuickAccessGui(ms);
		super.render(ms,mouseX, mouseY, p_render_3_);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if(this.mouseClickedQuickAccessQui(mouseX, mouseY))
			return true;
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	/**
	 * Renders and updates the quick access gui
	 */
	protected void renderQuickAccessGui(MatrixStack ms) {
		// Renders the GUI-background
		this.renderer.renderRect(ms,this.width / 2 - currentX / 2, this.height / 2 - currentY / 2, currentX, currentY,
				0xff1e252f);
		// Renders the title-background
		this.renderer.renderRect(ms,this.width / 2 - currentX / 2, this.height / 2 - currentY / 2, currentX, 20,
				0xFF000000);
		// Button-background
		this.renderer.renderRect(ms,this.width / 2 - currentX / 2, this.height / 2 - currentY / 2, 50, currentY,
				0xFF000000);
		// Renders the title
		AbstractGui.drawCenteredString(ms,this.font, this.guiTitle, this.width / 2 + 25, this.height / 2 - currentY / 2 + 6,
				0xFFFFFFFF);
		// Iterates over every menu button
		for (int i = 0; i < this.group.getSize(); i++) {
			// Renders the button
			this.renderer.renderRect(ms,this.width / 2 - currentX / 2, this.height / 2 - currentY / 2 + 20 + i * 20, 50,
					20, i == this.group.getIndex() ? 0xffEE5A24 : 0xFF000000);
			// Renders the text
			AbstractGui.drawCenteredString(ms,this.font, this.group.getGui(i).getGuiTitleShort(),
					this.width / 2 - currentX / 2 + 25, this.height / 2 - currentY / 2 + 26 + i * 20, 0xFFFFFFFF);
		}

		// Updates the gui's size
		this.updateSize();
	}

	/**
	 * Handles the click event for the quick access gui
	 * @param mX the mouse x position
	 * @param mY the mouse y position
	 * @return
	 */
	protected boolean mouseClickedQuickAccessQui(double mX,double mY) {
		// Iterates over every menu button
		for (int i = 0; i < this.group.getSize(); i++) {

			// Gets the buttons position
			int x = this.width / 2 - currentX / 2;
			int height = 20;
			int y = this.height / 2 - currentY / 2 + 20 + i * height;
			int width = 50;

			// Checks if the button got clicked
			if (mX > x && mX < x + width && mY > y && mY < y + height) {
				// Opens the gui
				this.group.update(i);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Updates the size of the gui
	 */
	private void updateSize() {
		// Checks if the size should be updated
		if (currentX != this.x || currentY != this.y) {
			//Scales the gui
			for (int i = 0; i < ModSettings.quickAccessGuiSpeed.value; i++) {
				// Formats the y axis
				if (currentY < this.y)
					currentY++;
				else if (currentY > this.y)
					currentY--;

				// Formats the x axis
				if (currentX < this.x)
					currentX++;
				else if (currentX > this.x)
					currentX--;

				// Executes the event
				this.handleSizeChange(currentX, currentY);
			}
		}
	}

	public void setGroup(GuiGroup<GuiQuickAccess> group) {
		this.group = group;
	}

	public String getGuiTitleShort() {
		return this.guiTitleShort;
	}

	/**
	 * Executes when the ingame-gui-size changes
	 *
	 * @param sizeX
	 *            the new size of x
	 * @param sizeY
	 *            the new size of y
	 */
	protected void handleSizeChange(int sizeX, int sizeY) {
		this.sizeChangeListeners.forEach((k,v)->k.move(v.apply(sizeX, sizeY)));
	}
	
	/**
	 * Registers a new size listener and the widget
	 * @param widget the widget to resize
	 * @param handler the handler for the resize
	 */
	protected void addWidgetWithListener(TmWidget widget,BiFunction<Integer, Integer, float[]> handler) {
		this.sizeChangeListeners.put(widget, handler);
		this.addWidget(widget);
	}

}

package de.whiletrue.toolsmod.gui.config;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.GuiGroup;
import de.whiletrue.toolsmod.gui.TmScreen;
import de.whiletrue.toolsmod.gui.config.sub.*;
import de.whiletrue.toolsmod.gui.config.sub.commands.ConfigGuiCommands;
import de.whiletrue.toolsmod.gui.config.sub.info.ConfigGuiInfos;
import de.whiletrue.toolsmod.gui.config.sub.modules.ConfigGuiModules;
import de.whiletrue.toolsmod.gui.widgets.TmBackgroundWidget;
import de.whiletrue.toolsmod.gui.widgets.TmButton;
import de.whiletrue.toolsmod.util.classes.RenderUtil;

public abstract class ConfigGui extends TmScreen {

	// All config guis
	public static final GuiGroup<ConfigGui> CONFIG_SCREENS = GuiGroup.of(
		new ConfigGuiModSettings(),
		new ConfigGuiModules(),
		new ConfigGuiCommands(),
		new ConfigGuiInfos()
	);

	// Width of the buttons for the configs
	private static final int CONFIG_BUTTON_WIDTH = 80;

	// Reference to the renderer
	protected RenderUtil renderer = RenderUtil.getInstance();

	// Gui starting position on the y axis
	protected int startY = 40;

	// Config gui language key
	private String keyName;
	// Config's switch button
	private ConfigSwitchButton switchBtn;

	public ConfigGui(String keyName) {
		this.keyName = keyName;
		this.switchBtn = new ConfigSwitchButton(0, 0, 0, 0, null, null).setBackgroundColor(0x5000a0ff)
				.setLineColor(0xFFffcf00);
	}

	@Override
	protected void init() {
		// Unfocuses if any widget was focused
		this.focused = null;

		// Adds the background overlay
		this.addWidget(new TmBackgroundWidget(0, this.startY, this.width, this.height - this.startY, 0x71000000));

		//Amount of screens
		int length = CONFIG_SCREENS.getSize();
		
		//Calculates the width of all widgets
		int fullWidth = CONFIG_BUTTON_WIDTH*length+5*(length-1);
		
		//Checks if the bar would overflow with the done button
		boolean overflow = (this.width-fullWidth-5)/2 < 60;
		
		// Adds the config buttons
		CONFIG_SCREENS.addToScreen((itm, len, cur) -> {	
			// Calculates the buttons x position
			int x = (overflow ? 5 : (this.width/2-fullWidth/2))+(CONFIG_BUTTON_WIDTH+5)*cur;

			// Adds the button
			this.addWidget(itm.getSwitchButton().setTextByKey(itm.getKeyName()).setConstandUnderlined(this.equals(itm))
					.setOnPress(btn -> {
						// Closes the current screen
						this.closeScreen();
						// Updates the menu
						CONFIG_SCREENS.update(cur);
					}));

			// Moves the button
			itm.getSwitchButton().move(x, 0, 80, this.startY);
		});

		// Adds the close button
		this.addWidget(
				new TmButton(this.width - 55, 10, 50, 20, "", i -> this.closeScreen()).setDisplayByKey("settings.back"));

	}

	@Override
	public void render(MatrixStack ms,int mX, int mY, float p_render_3_) {
		// Renders the default block-menu background
		this.renderDirtBackground(0);

		super.render(ms,mX, mY, p_render_3_);
	}

	public ConfigSwitchButton getSwitchButton() {
		return this.switchBtn;
	}

	public String getKeyName() {
		return this.keyName;
	}
}

package de.whiletrue.toolsmod.module.defined.tools.mapcreator.gui;

import de.whiletrue.toolsmod.gui.quickaccess.GuiQuickAccess;
import de.whiletrue.toolsmod.gui.widgets.TmSliderValues;
import de.whiletrue.toolsmod.gui.widgets.TmTextWidget;
import de.whiletrue.toolsmod.gui.widgets.TmUpdateButton;
import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.module.defined.tools.ModuleMapcreator;
import de.whiletrue.toolsmod.util.classes.TextUtil;

public class GuiQaMapcreatorCreate extends GuiQuickAccess {

	// Reference to the module
	private ModuleMapcreator module;

	// Delay slider
	private TmSliderValues<Float> delaySlider = new TmSliderValues<>(0, 0, 0, 0,
			new Float[] { 0f, 0.5f, 1f, 1.5f, 2f, 2.5f, 5f, 10f }, 0, (value, index) -> {
				return TextUtil.getInstance().getByKey("modules.mapcreator.gui.create.delay", value);
			});

	// Create button
	private TmUpdateButton btnCreate = (TmUpdateButton) new TmUpdateButton(0, 0, 0, 0, this::handleCreate);

	// Info view
	private TmTextWidget infoView = new TmTextWidget(0, 0, "", 0xFFffffff);

	public GuiQaMapcreatorCreate() {
		super("modules.mapcreator.gui.create.name", "modules.mapcreator.gui.create.name.short", 340, 200);
	}

	@Override
	protected void init() {
		// Gets the module refernece
		this.module = Toolsmod.getInstance().getModuleManager().getModuleByClass(ModuleMapcreator.class).get();

		// Adds the create button
		this.addWidgetWithListener(this.btnCreate
				.setEnabled(this.module.getLoadCommands().isEmpty())
				.setTooltippByKey("modules.mapcreator.gui.create.button.text"),
				(x, y) -> new float[] { this.width / 2 - 40 + 25, this.height / 2 + y * .2f, 80, 20 });

		// Adds the slider for the delay
		this.addWidgetWithListener(this.delaySlider,
				(x, y) -> new float[] { this.width / 2 - x / 2 + 100, this.height / 2 + y * .2f - 40, x - 150, 15 });
		this.delaySlider.move(this.width / 2 - 50, this.height / 2 - 20, 100, 15);

		// Adds the info view
		this.addWidgetWithListener(this.infoView.setText(""),
				(x, y) -> new float[] { this.width / 2 + 25, this.height / 2 + y * .2f + 30, });

		// Adds the info and stop button if any commands are ongoing
		if (!this.module.getLoadCommands().isEmpty()) {

			// Adds the stop button
			this.addWidgetWithListener(new TmUpdateButton(0, 0, 0, 0, btn -> {
				if (btn != null) {
					// Clears the commands
					this.module.getLoadCommands().clear();
					// Sends the info
					this.minecraft.displayGuiScreen(null);
					TextUtil.getInstance().sendMessage("modules.mapcreator.gui.create.stop");
				}
				return TextUtil.getInstance().getByKey("modules.mapcreator.gui.create.stop.button");
			}), (x, y) -> new float[] { this.width / 2 + 5, this.height / 2 - y * .25f, 40, 20 });

			// Adds the info text
			this.addWidgetWithListener(
					new TmTextWidget(0, 0, "", 0xff000000).setTextByKey("modules.mapcreator.gui.create.info.create"),
					(x, y) -> new float[] { this.width / 2 + 25, this.height / 2 - y * .32f });
		}
	}
	
	/**
	 * Executes when the create button gets pressed
	 */
	private String handleCreate(TmUpdateButton btn) {
		if(btn!=null)
			//Create the map
			this.infoView.setText(this.module.create(this.delaySlider.getStateValue()));
		
		return TextUtil.getInstance().getByKey("modules.mapcreator.gui.create.info.create.button");
	}

}

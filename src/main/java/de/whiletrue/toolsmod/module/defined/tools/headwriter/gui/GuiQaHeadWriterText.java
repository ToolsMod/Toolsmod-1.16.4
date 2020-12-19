package de.whiletrue.toolsmod.module.defined.tools.headwriter.gui;

import de.whiletrue.toolsmod.gui.quickaccess.GuiQuickAccess;
import de.whiletrue.toolsmod.gui.widgets.TmTextfield;
import de.whiletrue.toolsmod.gui.widgets.TmUpdateButton;
import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.module.defined.tools.ModuleHeadWriter;
import de.whiletrue.toolsmod.util.classes.TextUtil;

public class GuiQaHeadWriterText extends GuiQuickAccess {

	// Text-field with the text that should be written
	private TmTextfield text = new TmTextfield(0, 0, 0, 0, "", null).setMaxStringLength(Integer.MAX_VALUE);

	// Reference to the module
	private ModuleHeadWriter module;

	public GuiQaHeadWriterText() {
		super("modules.headwriter.gui.text.name", "modules.headwriter.gui.text.name.short", 280, 180);
	}

	@Override
	protected void init() {
		// Gets the module
		this.module = Toolsmod.getInstance().getModuleManager().getModuleByClass(ModuleHeadWriter.class).get();

		// Adds the text-field
		this.addWidgetWithListener(this.text,(x,y)->new float[] {
			this.width / 2 + 25 - x * .4f + 50,
			this.height / 2 - y * .2f,
			x * .8f - 100,
			y * .08f
		});

		// Adds the start button
		this.addWidget(new TmUpdateButton(this.width / 2 - 5, this.height / 2 + 20, 60, 20, btn -> {
			if (btn != null && !this.text.getText().isEmpty())
				// Starts writing
				this.module.startWriting(this.text.getText());
			return TextUtil.getInstance().getByKey("modules.headwriter.start");
		}));
	}

}

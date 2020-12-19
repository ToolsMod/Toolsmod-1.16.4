package de.whiletrue.toolsmod.module.defined.tools.bannerwriter.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.quickaccess.GuiQuickAccess;
import de.whiletrue.toolsmod.gui.widgets.TmBackgroundWidget;
import de.whiletrue.toolsmod.gui.widgets.TmTextWidget;
import de.whiletrue.toolsmod.gui.widgets.TmTextfield;
import de.whiletrue.toolsmod.gui.widgets.TmUpdateButton;
import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.module.defined.tools.ModuleBannerWriter;
import de.whiletrue.toolsmod.module.defined.tools.bannerwriter.EnumDye;
import de.whiletrue.toolsmod.util.classes.JavaUtil;
import de.whiletrue.toolsmod.util.classes.TextUtil;

public class GuiQaBannerWriter extends GuiQuickAccess {

	// Textfield with the text that should be written
	private TmTextfield textField = new TmTextfield(0, 0, 0, 0, "", null).setMaxStringLength(Integer.MAX_VALUE);

	// Selected letter-color
	private EnumDye letterColor = EnumDye.RED;
	// Selected background-color
	private EnumDye backgroundColor = EnumDye.LIME;
	//Select letter and background name
	private String letterName,backgroundName;

	// Reference to the module
	private ModuleBannerWriter module;

	public GuiQaBannerWriter() {
		super("modules.bannerwriter.gui.name", "modules.bannerwriter.gui.name.short", 280, 180);
	}

	@Override
	protected void init(){
		//Gets the dye
		this.updateDye();
		
		// Gets the module
		this.module = Toolsmod.getInstance().getModuleManager().getModuleByClass(ModuleBannerWriter.class).get();

		// Adds the text-field
		this.addWidgetWithListener(this.textField,(x,y)->new float[] {
			this.width / 2 + 25 - x * .4f + 50,
			this.height / 2 - y * .3f,
			x * .8f - 100,
			y * .08f
		});

		//Adds the color background
		this.addWidget(new TmBackgroundWidget(this.width / 2 - 25, this.height / 2 - 20, 100, 20, 0x60000000));
		this.addWidget(new TmBackgroundWidget(this.width / 2 - 25, this.height / 2 + 20, 100, 20, 0x60000000));
		
		//Adds the text infos
		this.addWidget(new TmTextWidget(this.width / 2 + 25, this.height / 2 - 32, "", 0xFFffffff)
				.setTextByKey("modules.bannerwriter.gui.color.letter"));
		this.addWidget(new TmTextWidget(this.width / 2 + 25, this.height / 2 + 8, "", 0xFFffffff)
				.setTextByKey("modules.bannerwriter.gui.color.background"));
		
		// Adds the back-letter button
		this.addWidget(new TmUpdateButton(this.width / 2 - 37, this.height / 2 - 20, 12, 20, btn -> {
			if (btn != null) {
				// Rotates to the previous color
				this.letterColor = JavaUtil.getInstance().getEnumPre(this.letterColor);
				this.updateDye();
			}
			return "<";
		}));
		// Adds the forward-letter button
		this.addWidget(new TmUpdateButton(this.width / 2 + 75, this.height / 2 - 20, 12, 20, btn -> {
			if (btn != null) {
				// Rotates to the next color
				this.letterColor = JavaUtil.getInstance().getEnumNext(this.letterColor);
				this.updateDye();
			}
			return ">";
		}));

		// Adds the back-background button
		this.addWidget(new TmUpdateButton(this.width / 2 - 37, this.height / 2 + 20, 12, 20, btn -> {
			if (btn != null) {
				// Rotates to the previous color
				this.backgroundColor = JavaUtil.getInstance().getEnumPre(this.backgroundColor);
				this.updateDye();				
			}
			return "<";
		}));
		// Adds the forward-background button
		this.addWidget(new TmUpdateButton(this.width / 2 + 75, this.height / 2 + 20, 12, 20, btn -> {
			if (btn != null) {
				// Rotates to the next color
				this.backgroundColor = JavaUtil.getInstance().getEnumNext(this.backgroundColor);
				this.updateDye();
			}
			return ">";
		}));

		// Adds the start button
		this.addWidget(new TmUpdateButton(this.width / 2 - 5, this.height / 2 + 50, 60, 20, btn -> {
			if (btn != null && !this.textField.getText().isEmpty())
				// Starts writing
				this.module.startWriting(this.textField.getText(), this.letterColor, this.backgroundColor);
			return TextUtil.getInstance().getByKey("modules.bannerwriter.gui.start");
		}));

		super.init();
	}

	/**
	 * Updates the dyes
	 */
	public void updateDye() {
		this.letterName = this.letterColor.getName();
		this.backgroundName = this.backgroundColor.getName();
	}

	@Override
	public void render(MatrixStack ms,int mouseX, int mouseY, float p_render_3_) {
		super.render(ms,mouseX, mouseY, p_render_3_);
		// Render the selected letter color
		this.drawCenteredString(ms,this.font, this.letterName, this.width / 2 + 25, this.height / 2 - 14,
				this.letterColor.getColor());

		// Renders the selected background color
		this.drawCenteredString(ms,this.font, this.backgroundName, this.width / 2 + 25, this.height / 2 + 26,
				this.backgroundColor.getColor());
	}

}

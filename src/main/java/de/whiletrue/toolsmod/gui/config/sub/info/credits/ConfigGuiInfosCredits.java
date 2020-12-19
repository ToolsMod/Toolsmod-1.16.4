package de.whiletrue.toolsmod.gui.config.sub.info.credits;

import java.util.stream.StreamSupport;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.TmScreen;
import de.whiletrue.toolsmod.gui.config.ConfigGui;
import de.whiletrue.toolsmod.gui.widgets.TmBackgroundWidget;
import de.whiletrue.toolsmod.gui.widgets.TmButton;
import de.whiletrue.toolsmod.gui.widgets.TmTextWidget;
import de.whiletrue.toolsmod.gui.widgets.rounding.listscaleable.ScaleableListView;
import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.util.TextAlign;
import de.whiletrue.toolsmod.util.classes.FileUtil;
import net.minecraft.util.ResourceLocation;

public class ConfigGuiInfosCredits extends TmScreen {

	// Credits file
	private final ResourceLocation creditsFile = new ResourceLocation(Toolsmod.ID, "credits.json");

	// List with the credits
	private ScaleableListView<CreditView> list;

	public ConfigGuiInfosCredits() {
		try {
			CreditView[] credits = StreamSupport.stream(
					// Loads all credits
					FileUtil.getInstance().loadFromRSCAsJson(this.creditsFile).get().getAsJsonArray().spliterator(),
					false).map(i -> {
						try {
							return new CreditView(new Credit(i.getAsJsonObject()));
						} catch (Exception e) {
							return null;
						}
					}).toArray(CreditView[]::new);

			// Checks if any credits got loaded
			if (credits.length > 0)
				// Creates the list
				this.list = new ScaleableListView<CreditView>(0, 0, 0, 0)
						// Sets the properties
						.setScrollStrength(5)
						// Adds the credits
						.setItems(credits);

		} catch (Exception e) {}
	}

	@Override
	protected void init() {

		// Adds the overlay
		this.addWidget(new TmBackgroundWidget(0, 40, this.width, this.height - 40, 0xa0000000));

		// Adds the thanks text
		this.addWidget(new TmTextWidget(this.width / 2, 20, "Thanks to...", 0xff0093ff)
				.setScale(2)// Updates the scale
				.setAlignY(TextAlign.MIDDLE));// Sets the middle align

		// Adds the close button
		this.addWidget(
				new TmButton(this.width - 55, 10, 50, 20, "", i -> this.closeScreen()).setDisplayByKey("gui.config.credits.done"));

		// Checks if the credits got loaded
		if (this.list != null) {
			// Adds the list
			this.addWidget(this.list);
			this.list.move((int) (this.width * .25), 50, (int) (this.width * .5), this.height - 55);
		} else
			// Adds the error info
			this.addWidget(new TmTextWidget(this.width / 2, this.height / 2, null, 0xffFF3838)
					.setScale(2) //Updates the scale
					.setTextByKey("gui.config.credits.failed")); //Set the text
	}

	@Override
	public void render(MatrixStack ms,int mX, int mY, float ticks) {
		this.renderBackground(ms);
		super.render(ms,mX, mY, ticks);
	}
	
	@Override
	public void closeScreen() {
		//Opens the config screen
		ConfigGui.CONFIG_SCREENS.open();
	}
}

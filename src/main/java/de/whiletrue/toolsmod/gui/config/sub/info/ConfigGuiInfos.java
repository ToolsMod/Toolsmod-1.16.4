package de.whiletrue.toolsmod.gui.config.sub.info;

import de.whiletrue.toolsmod.gui.config.ConfigGui;
import de.whiletrue.toolsmod.gui.config.sub.info.changelog.ConfigGuiInfosChangelog;
import de.whiletrue.toolsmod.gui.config.sub.info.credits.ConfigGuiInfosCredits;
import de.whiletrue.toolsmod.gui.widgets.TmButton;
import de.whiletrue.toolsmod.gui.widgets.TmLinkButton;
import de.whiletrue.toolsmod.gui.widgets.TmTextWidget;
import de.whiletrue.toolsmod.gui.widgets.rounding.GuiNode;
import de.whiletrue.toolsmod.mod.Toolsmod;

public class ConfigGuiInfos extends ConfigGui {

	// All static assets
	private final String[] supportInfo = "If you want to support this mod,\nplease read all information on\nthe official Toolsmod website."
			.split("\n");
	private final String supportButton = "Support";

	// Link references
	private final String referenceDiscord = "discord.gg/xq2yBqM";
	private final String referenceGithub = "github.com/ToolsMod";
	private final String referenceWebsite = "toolsmod.github.io";
	private final String referenceSupport = "support";

	public ConfigGuiInfos() {
		super("gui.config.nav.info");
	}

	@Override
	protected void init() {
		super.init();

		// Gets all nodes
		GuiNode requestNode = this.getRequestNode();
		GuiNode supportNode = this.getSupportNode();

		// Calculates the max height of both nodes
		int h = Math.max(requestNode.getHeight(), supportNode.getHeight());

		// Calculates space between the nodes
		int spaceX = (this.width - requestNode.getWidth() - supportNode.getWidth()) / 3;

		// Calculates the height
		int spaceY = (this.height - 50 - h) / 3;

		// Init's the nodes
		requestNode.init(spaceX, spaceY + 50);
		supportNode.init(spaceX * 2 + requestNode.getWidth(), spaceY + 50);

		// Adds the credits button
		this.addWidget(new TmButton(this.width / 2 - 82, 50 + spaceY * 2 + h, 80, 20, null,
				i -> this.minecraft.displayGuiScreen(new ConfigGuiInfosCredits()))
						.setDisplayByKey("gui.config.info.credits"));
		
		//Adds the change-log button
		this.addWidget(
			new TmButton(this.width/2+2, 50+spaceY*2+h, 80, 20, null,
			i -> this.minecraft.displayGuiScreen(new ConfigGuiInfosChangelog()))
			.setDisplayByKey("gui.config.info.changelog")//Sets the text
			.setEnabled(Toolsmod.getInstance().getUpdater().getChangelog().isLoaded())//Enables the button
		);
	}

	private GuiNode getSupportNode() {
		// Width and height of the node
		int w = 180;
		int h = 60;

		return new GuiNode(w, h, (x, y) -> {
			// Adds the info
			this.addWidget(new TmTextWidget(x + w / 2, y, null, 0xFFffffff).setText(this.supportInfo));

			// Adds the button
			this.addWidget(new TmLinkButton(x + w / 2 - 30, y + h - 20, 60, 20, this.supportButton,
					this.referenceWebsite + '/' + this.referenceSupport).setDisplayColor(0xE9B714));
		});
	}

	private GuiNode getRequestNode() {
		// With and height of the node
		int w = 200;
		int h = 50;

		return new GuiNode(w, h, (x, y) -> {

			// Creates the discord button
			this.addWidget(new TmLinkButton(x, y + h - 20, 60, 20, "Discord", this.referenceDiscord)
					.setDisplayColor(0x4764CB));

			// Creates the website button
			this.addWidget(new TmLinkButton(x + w / 2 - 30, y + h - 20, 60, 20, "Website", this.referenceWebsite)
					.setDisplayColor(0xE9B714));

			// Creates the discord button
			this.addWidget(new TmLinkButton(x + w - 60, y + h - 20, 60, 20, "Github", this.referenceGithub));

			// Creates the text info
			this.addWidget(new TmTextWidget(x + w / 2, y, null, 0xffFFFFFF).setTextByKey("gui.config.info.info"));
		});
	}
}

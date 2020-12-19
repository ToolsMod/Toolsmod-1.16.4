package de.whiletrue.toolsmod.module.defined.tools;

import java.util.ArrayList;
import java.util.List;

import de.whiletrue.toolsmod.gui.GuiGroup;
import de.whiletrue.toolsmod.gui.quickaccess.GuiQuickAccess;
import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.module.ModuleCategory;
import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.module.defined.tools.bannerwriter.EnumBannertypes;
import de.whiletrue.toolsmod.module.defined.tools.bannerwriter.EnumDye;
import de.whiletrue.toolsmod.module.defined.tools.bannerwriter.gui.GuiQaBannerWriter;
import de.whiletrue.toolsmod.util.classes.ItemUtil;
import de.whiletrue.toolsmod.util.classes.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;

public class ModuleBannerWriter extends Module {

	//Gui to enable the module
	private static GuiGroup<GuiQuickAccess> ENABLE_GUI = GuiGroup.of(
		new GuiQaBannerWriter()
	);
	
	// List with all heads that are remaining
	private List<ItemStack> remaningBanners = new ArrayList<>();

	// If the writing is enabled
	private boolean enabled;

	// Selected
	private String currentText;
	private String letterName,backgroundName;

	public ModuleBannerWriter() {
		super("BannerWriter", ModuleCategory.TOOLS, false);
	}

	@Override
	public void onEnable() {
		// Checks if the module got enabled from the gui
		if (!this.enabled) {
			this.disable();
			//Opens the gui
			ENABLE_GUI.open();
			return;
		}

		// Reset
		this.enabled = false;
		// Start the the first head
		this.giveNextBanner();
	}

	@Override
	public void onPlace(EntityPlaceEvent event) {
		// Checks if player is creative
		if (!Minecraft.getInstance().player.abilities.isCreativeMode) {
			TextUtil.getInstance().sendError("modules.bannerwriter.error.gm");
			this.disable();
			return;
		}

		// Checks the item
		if (!(this.mc.player.getHeldItemMainhand().getItem() instanceof BannerItem))
			return;

		// Gets the next item
		this.giveNextBanner();
	}

	@Override
	public void onDisable() {
		// Reset
		this.enabled = false;
	}

	@Override
	public String[] onInformationEvent(char main,char sec) {
		return new String[] {
			String.format("§%cText: §%c%s",main,sec, this.currentText),
			String.format("§%cColors: §%c%s | %s",main,sec,this.letterName, this.backgroundName),
			String.format("§%cRemaining: §%c%s",main,sec,this.remaningBanners.size() + 1)
		};
	}

	/**
	 * Gives the next head to the player
	 */
	private void giveNextBanner() {
		// Checks if all got placed
		if (this.remaningBanners.isEmpty()) {
			this.disable();
			ItemUtil.getInstance().setItem(ItemStack.EMPTY);
			return;
		}

		// Sends the next banner
		ItemUtil.getInstance().setItem(this.remaningBanners.get(0));
		this.remaningBanners.remove(0);

		//Updates the info display
		Toolsmod.getInstance().getModuleManager().updateInfoDisplay();
	}

	/**
	 * Starts the banner-writer
	 * 
	 * @param text
	 */
	public void startWriting(String text, EnumDye letter, EnumDye background) {
		// Disables the module
		this.disable();

		// Checks if player is creative
		if (!Minecraft.getInstance().player.abilities.isCreativeMode) {
			TextUtil.getInstance().sendError("modules.bannerwriter.error.gm");
			return;
		}

		// Clears the list
		this.remaningBanners.clear();

		// Sets the selected
		this.currentText = text;
		this.letterName=letter.getName();
		this.backgroundName=background.getName();

		// For every characters
		for (char c : text.toLowerCase().toCharArray()) {

			// Gets the nbt tag
			CompoundNBT nbt = EnumBannertypes.getByChar(c).getTag(letter, background, true);

			// Creates the stack
			ItemStack head = new ItemStack(background.getBanner());
			head.setTag(nbt);
			head.setDisplayName(new StringTextComponent(
					String.format("§8[§%c"+this.getName()+"§8]: §%c%s", Toolsmod.COLOR_MAIN, Toolsmod.COLOR_SECONDARY, text)));

			// Adds the stack
			this.remaningBanners.add(head);
		}

		// Closes the gui
		this.mc.displayGuiScreen(null);

		// Starts the module
		this.enabled = true;
		this.enable();
	}

}

package de.whiletrue.toolsmod.module.defined.tools;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import de.whiletrue.toolsmod.gui.GuiGroup;
import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.module.ModuleCategory;
import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.module.defined.tools.asedit.AsEditManager;
import de.whiletrue.toolsmod.module.defined.tools.asedit.EditableArmorStand;
import de.whiletrue.toolsmod.module.defined.tools.asedit.EnumMoveType;
import de.whiletrue.toolsmod.module.defined.tools.asedit.gui.GuiAsEdit;
import de.whiletrue.toolsmod.module.defined.tools.asedit.gui.defined.GuiAsEditAdjust;
import de.whiletrue.toolsmod.module.defined.tools.asedit.gui.defined.GuiAsEditCreate;
import de.whiletrue.toolsmod.module.defined.tools.asedit.gui.defined.GuiAsEditImage;
import de.whiletrue.toolsmod.module.defined.tools.asedit.gui.defined.GuiAsEditProjects;
import de.whiletrue.toolsmod.module.defined.tools.asedit.gui.defined.GuiAsEditSettings;
import de.whiletrue.toolsmod.module.defined.tools.asedit.projects.AsEditProjectManager;
import de.whiletrue.toolsmod.settings.defined.SettingBool;
import de.whiletrue.toolsmod.settings.defined.SettingKeybind;
import de.whiletrue.toolsmod.util.Keybind;
import de.whiletrue.toolsmod.util.classes.ItemUtil;
import de.whiletrue.toolsmod.util.classes.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.world.WorldEvent.Load;

public class ModuleAsEdit extends Module {

	// If the gui's item should be extended
	public SettingBool sExtendedList = new SettingBool().name("listExtended").standard(false);

	// On which key the gui opens
	public SettingKeybind sGuiBind = new SettingKeybind().name("guiKeybind")
			.standard(new Keybind(GLFW.GLFW_KEY_F, KeyModifier.NONE));

	//If textfield should be used instead of sliders
	public SettingBool sTextRotation = new SettingBool()
			.name("textRotaion")
			.standard(false);
	
	// AsEdit manager
	private final AsEditManager manager = new AsEditManager();

	// AsProject manager
	private final AsEditProjectManager pjManager;

	// List with the remaining stands that must be placed
	private List<ItemStack> remaningStands = new ArrayList<>();
	// If the creation is running
	private boolean placing;
	// If the next stand should be given
	private boolean nextStand;

	// All guis for this module
	public final GuiGroup<GuiAsEdit> guis = GuiGroup.of(
		new GuiAsEditSettings(),
		new GuiAsEditAdjust(),
		new GuiAsEditProjects(),
		new GuiAsEditCreate(),
		new GuiAsEditImage()
	);

	public ModuleAsEdit() {
		super("AsEdit", ModuleCategory.TOOLS, false);

		// Creates the project manager
		this.pjManager = new AsEditProjectManager(this.manager);
	}

	@Override
	public void onWorldLoad(Load evt) {
		this.disable();
	}

	@Override
	public void onEnable() {
		// Resets the manager
		this.manager.deleteAllStands();

		// Resets the placer
		this.remaningStands.clear();
		this.placing = false;
	}

	@Override
	public void onDisable() {
		// Resets the manager
		this.manager.deleteAllStandsNoCheck();
		this.pjManager.setLoaded(null);

		// Removes all stacks
		this.remaningStands.clear();
	}

	@Override
	public void onTick(ClientTickEvent event) {
		// Checks if the next item should be given
		if (this.nextStand) {
			this.giveNextStand();
			this.nextStand = false;
		}
	}

	@Override
	public boolean onClientPacket(IPacket<IServerPlayNetHandler> packet) {
		// Checks if the module has stands to place
		if (this.placing) {
			if (packet instanceof CPlayerTryUseItemOnBlockPacket) {
				CPlayerTryUseItemOnBlockPacket p = (CPlayerTryUseItemOnBlockPacket) packet;
				// Checks if the module has stands to place
				if (!this.placing)
					return true;

				// Checks if the stand got placed
				if (!this.canStandBePlaced(p.getHand()))
					return true;

				// Checks if player is creative
				if (!Minecraft.getInstance().player.abilities.isCreativeMode) {
					TextUtil.getInstance().sendError("modules.asedit.gm");
					this.disable();
					return true;
				}

				// Checks the item
				if (!(this.mc.player.getHeldItemMainhand().getItem().equals(Items.ARMOR_STAND)))
					return true;

				this.nextStand = true;

				return true;
			}
		} else if (
		// Checks if the packet is a use entity packet
		packet instanceof CUseEntityPacket &&
		// Checks if the event should be cancelled
				this.manager.handleUseStand((CUseEntityPacket) packet))
			return false;

		return true;
	}

	@Override
	public void onScrollMouse(MouseScrollEvent event) {
		// Checks if the module has stands to place
		if (this.placing)
			return;

		// Executes the event and checks if it should be cancelled
		if (this.manager.handleMouseScroll((float) event.getScrollDelta()))
			event.setCanceled(true);
	}

	@Override
	public String[] onInformationEvent(char main, char sec) {
		// Check if stands should be placed
		if (this.placing)
			return new String[] { String.format("§%cRemaining: §%c%d", main, sec, this.remaningStands.size() + 1) };

		// Different axis
		char[] axisStrings = { 'X', 'Y', 'Z' };

		// Gets the info's
		EnumMoveType mode = this.manager.getMode();
		float speed = this.manager.getSpeed();
		int axis = this.manager.getAxis();

		// Gets the selected stand
		EditableArmorStand as = this.manager.getSelectedStand();

		// Render-strings
		return new String[] {
				String.format("§%cProject: §%c%s", main, sec,
						this.pjManager.isLoaded() ? this.pjManager.getLoaded().getName(-1) : "None"),
				"", String.format("§%cSelected: §%c%s", main, sec, as == null ? "Multiple" : as.getReferenceName()),
				String.format("§%cAdjusting: §%c%s", main, sec, mode != null ? mode.getName() : "Nothing"),
				(mode == null ? null : String.format("§%cSpeed: §%c%s", main, sec, speed)),
				(EnumMoveType.ROTATION.equals(mode) || mode == null ? null
						: String.format("§%cAxis: §%c%c", main, sec, axisStrings[axis])) };
	}

	@Override
	public void onKeyToggled(KeyInputEvent event) {
		// Checks if the key for opening is pressed
		if (this.sGuiBind.value.isKeycodeMatching(event.getKey()))
			// Checks if stands are needed to be placed
			if (!this.placing)
				// Opens the GUI
				this.guis.open();
			else
				// Sends the error
				TextUtil.getInstance().sendError("modules.asedit.left", this.remaningStands.size() + 1);
	}

	/**
	 * Executes when the user want's to start the placer
	 * 
	 * @returns if the start was successful
	 */
	public boolean startPlaceing() {
		// Checks if player is creative
		if (!Minecraft.getInstance().player.abilities.isCreativeMode)
			return false;

		// Sets the new stacks
		this.remaningStands.clear();
		this.remaningStands.addAll(this.manager.getConvertedStands());

		// Activates the placer
		this.placing = true;
		this.manager.setMode(null);

		// Removes all stands
		this.manager.deleteAllStandsNoCheck();

		// Start with the first stand
		this.giveNextStand();
		return true;
	}

	/**
	 * Gives the next head to the player
	 */
	private void giveNextStand() {
		// Checks if all got placed
		if (this.remaningStands.isEmpty()) {
			this.disable();
			ItemUtil.getInstance().setItem(ItemStack.EMPTY);
			return;
		}

		// Sends the next stand
		ItemUtil.getInstance().setItem(this.remaningStands.get(0));
		this.remaningStands.remove(0);

		// Updates the info display
		Toolsmod.getInstance().getModuleManager().updateInfoDisplay();
	}

	/**
	 * @param hand
	 * @return if the player can place the armor stand at the given position
	 */
	public boolean canStandBePlaced(Hand hand) {

		// Gets the use context
		ItemUseContext iuc = new ItemUseContext(this.mc.player, hand, (BlockRayTraceResult) this.mc.objectMouseOver);

		// Gets all values to calculate
		BlockItemUseContext blockitemusecontext = new BlockItemUseContext(iuc);
		BlockPos blockpos = blockitemusecontext.getPos();
		BlockPos blockpos1 = blockpos.up();

		// Checks if the stand can be placed in the context of blocks being in the way
		if (blockitemusecontext.canPlace()
				&& this.mc.world.getBlockState(blockpos1).isReplaceable(blockitemusecontext)) {
			// Gets the coordinates
			double d0 = blockpos.getX();
			double d1 = blockpos.getY();
			double d2 = blockpos.getZ();

			// Gets all entity's that might be in the way
			List<Entity> list = this.mc.world.getEntitiesWithinAABBExcludingEntity((Entity) null,
					new AxisAlignedBB(d0, d1, d2, d0 + 1.0D, d1 + 2.0D, d2 + 1.0D));

			// Checks if any entity's are in the way
			return list.isEmpty();
		}

		return false;
	}

	public AsEditManager getManager() {
		return this.manager;
	}

	public AsEditProjectManager getPjManager() {
		return this.pjManager;
	}
}

package de.whiletrue.toolsmod.module.defined.tools.mapcreator.gui;

import de.whiletrue.toolsmod.gui.quickaccess.GuiQuickAccess;
import de.whiletrue.toolsmod.gui.widgets.rounding.listmultirow.MultirowListView;
import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.module.defined.tools.ModuleMapcreator;
import de.whiletrue.toolsmod.module.defined.tools.mapcreator.BlockSelection;
import net.minecraft.block.Block;

public class GuiQaMapcreatorColor extends GuiQuickAccess {

	// Block pallet
	private MultirowListView<BlockSelectionItem> blockPallet = new MultirowListView<BlockSelectionItem>(
			0, 0, 0, 0).setListFormatting(5, 5, 3, 20)
			.setScrollStrength(5);
	// Selection pallet
	private MultirowListView<BlockItem> selectPallet = new MultirowListView<BlockItem>(0, 0, 0, 0).setListFormatting(5, 5,
			6, 20)
			.setScrollStrength(5);

	// Reference to the module
	private ModuleMapcreator module;

	// Current selected color
	private BlockSelection selected;

	public GuiQaMapcreatorColor() {
		super("modules.mapcreator.gui.color.name", "modules.mapcreator.gui.color.name.short", 380, 240);
	}

	@Override
	protected void init() {
		// Loads the module
		this.module = Toolsmod.getInstance().getModuleManager().getModuleByClass(ModuleMapcreator.class).get();

		// Prepares the pallet
		this.blockPallet.setItems(this.module.getColorPalete().values().stream()
				.map(i -> new BlockSelectionItem(i, this::onSelectPalete, i.equals(this.selected)))
				.toArray(BlockSelectionItem[]::new));

		// Sets the selected pallet
		if (this.selected != null)
			this.selectPallet.setItems(this.selected.getAvailableBlocks().stream()
					.map(i -> new BlockItem(i, this::onSelectBlock, i.equals(this.selected.getSelected())))
					.toArray(BlockItem[]::new));

		// Adds the lists
		this.addWidgetWithListener(this.blockPallet, (x,
				y) -> new float[] { this.width / 2 - x / 2 + 65, this.height / 2 - y / 2 + 60, x / 2 - 50, y - 75 });

		this.addWidgetWithListener(this.selectPallet,
				(x, y) -> new float[] { this.width / 2 + 35, this.height / 2 - y / 2 + 60, x / 2 - 50, y - 75 });
	}

	/**
	 * Handler for the pallet selection event
	 * @param select the new select pallet
	 */
	private void onSelectPalete(BlockSelection select) {
		this.selected = select;

		// Updates the selection
		this.blockPallet.getViews().forEach(i -> i.setSelected(i.getItem().equals(this.selected)));
		
		//Updates the block list
		this.selectPallet.setItems(this.selected.getAvailableBlocks().stream()
				.map(i -> new BlockItem(i, this::onSelectBlock, i.equals(this.selected.getSelected())))
				.toArray(BlockItem[]::new));
	}

	/**
	 * Handler for the block selection event
	 * @param block the selected block
	 */
	private void onSelectBlock(Block block) {
		//Updates the selected block
		this.selected.setSelected(block);
		
		//Updates the selection
		this.selectPallet.getViews().forEach(i->i.setSelected(i.getItem().equals(block)));
		
		//Updates the gui
		this.module.getIngameGui().update();
	}
}

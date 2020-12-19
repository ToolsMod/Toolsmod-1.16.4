package de.whiletrue.toolsmod.module.defined.tools.headwriter.gui;

import de.whiletrue.toolsmod.gui.quickaccess.GuiQuickAccess;
import de.whiletrue.toolsmod.gui.widgets.TmTextfield;
import de.whiletrue.toolsmod.gui.widgets.rounding.listmultirow.MultirowListView;
import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.module.defined.tools.ModuleHeadWriter;

public class GuiQaHeadWriterType extends GuiQuickAccess {

	// List with all alphabets
	private MultirowListView<AlphabetItem> list = new MultirowListView<AlphabetItem>(0, 0, 0, 0)
			.setListFormatting(5, 5, 3, 20).setScrollStrength(4).setValidator(i -> i.isValid(this.searchBar.getText()));

	// Reference to the module
	private ModuleHeadWriter module;

	// Search bar
	private TmTextfield searchBar = new TmTextfield(0, 0, 0, 0, "", i -> this.list.updateValidation());

	public GuiQaHeadWriterType() {
		super("modules.headwriter.gui.type.name", "modules.headwriter.gui.type.name.short", 300, 200);
	}

	@Override
	protected void init() {
		// Gets the module reference
		this.module = Toolsmod.getInstance().getModuleManager().getModuleByClass(ModuleHeadWriter.class).get();

		// Adds the search bar
		this.addWidgetWithListener(this.searchBar,(x,y)->new float[]{
			this.width / 2 - x * .3f + 50,
			this.height / 2 - y * .36f,
			x * .6f - 50,
			y * .08f
		});
		// Adds the list
		this.addWidgetWithListener(this.list.setItems(this.module.loadedAlphabets.stream()
				.map(i -> new AlphabetItem(i, this::onSelect).setSelected(i == this.module.selected))
				.toArray(AlphabetItem[]::new)),
				(x,y)->new float[] {
					this.width / 2 - x / 2 + 60,
					this.height / 2 - y / 2 + 60,
					x - 70,
					y - 75
				});

	}

	/**
	 * Handler when an other alphabet gets selected
	 * @param item the new selected item
	 */
	private void onSelect(AlphabetItem item) {
		//Updates the selection
		this.module.selected=item.getItem();
		
		//Updates all items
		this.list.getViews().forEach(i->i.setSelected(i.getItem()==this.module.selected));
	}
}

package de.whiletrue.toolsmod.module.defined.tools.asedit.gui.defined;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import de.whiletrue.toolsmod.gui.widgets.TmBackgroundWidget;
import de.whiletrue.toolsmod.gui.widgets.TmItemButton;
import de.whiletrue.toolsmod.gui.widgets.TmTextWidget;
import de.whiletrue.toolsmod.gui.widgets.TmTextfield;
import de.whiletrue.toolsmod.gui.widgets.TmUpdateButton;
import de.whiletrue.toolsmod.gui.widgets.rounding.GuiNode;
import de.whiletrue.toolsmod.module.defined.tools.asedit.EditableArmorStand;
import de.whiletrue.toolsmod.module.defined.tools.asedit.EnumEditStandEquip;
import de.whiletrue.toolsmod.module.defined.tools.asedit.gui.AsEditToggleButton;
import de.whiletrue.toolsmod.module.defined.tools.asedit.gui.GuiAsEdit;
import de.whiletrue.toolsmod.util.classes.TextUtil;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;

public class GuiAsEditSettings extends GuiAsEdit{

	//Selected armor stand
	@Nullable
	private EditableArmorStand stand;
	
	// Preset slot (Settings)
	private EnumEditStandEquip slot = EnumEditStandEquip.MAINHAND;

	// List with all toggle buttons
	private AsEditToggleButton[] toggleButtons = new AsEditToggleButton[] {
			new AsEditToggleButton(s -> !s.getAttr(8), (s, b) -> s.setAttr(8, !b), "baseplate"),
			new AsEditToggleButton(s -> s.getAttr(4), (s, b) -> s.setAttr(4, b), "arms"),
			new AsEditToggleButton(s -> s.getAttr(1), (s, b) -> s.setAttr(1, b), "small"),
			new AsEditToggleButton(s -> !s.hasNoGravity(), (s, b) -> s.setNoGravity(!b), "gravity"),
			new AsEditToggleButton(s -> !s.isInvisible(), (s, b) -> s.setInvisible(!b), "visible"),
			new AsEditToggleButton(s -> !s.isInvulnerable(), (s, b) -> s.setInvulnerable(!b), "vulnerable"),
			new AsEditToggleButton(s -> s.areSlotsDisabled(), (s, b) -> s.setDisabledSlots(b), "disabledslots"),
			new AsEditToggleButton(s -> s.isGlowingNatrual(), (s, b) -> s.setGlowing(b), "glowing") };
	
	public GuiAsEditSettings() {
		super("settings.name");
	}

	@Override
	protected void init() {
		super.init();
		
		//Gets the selected stand
		this.stand=this.manager.getSelectedStand();

		// Checks if there is enough place to add the list
		if (this.overlayWidth > 400)
			//Appends the stand list
			this.appendList();
		
		// Checks if only a single stand got selected
		if (this.stand != null)
			this.initSingle();
		else
			this.initMulti();
	}
	
	/**
	 * Init when multiple stands are selected
	 */
	private void initMulti() {
		//Gets the nodes
		GuiNode settingsNode = this.addSettingsButtons(105, (btn, set) -> {

			// Gets the amount of enabled/disabled stands
			Map<Boolean, List<EditableArmorStand>> values = this.manager.getStands()
					// For all stands
					.stream()
					// Filters all selected
					.filter(i -> i.isSelected())
					// Splits them into enabled and disabled
					.collect(Collectors.groupingBy(btn::get));

			// Amount of positive and negative values
			int plus = values.containsKey(true) ? values.get(true).size() : 0;
			int minus = values.containsKey(false) ? values.get(false).size() : 0;

			// If the positive is bigger
			boolean positive = plus >= minus;

			// Gets the percentage
			float perc = (float) (positive ? plus : minus) / (float) (plus + minus);

			// If the button got pressed
			if (set) {
				boolean wrapper = positive;
				// Sets all stands of the opposite
				this.manager.getStands().stream().filter(i -> i.isSelected()).forEach(i -> btn.set(i, !wrapper));
				positive = !positive;
				perc = 1;
			}

			// Returns the formatted text
			return String.format("%s §%c %d%%",
					TextUtil.getInstance().getByKey("modules.asedit.gui.settings.settings." + btn.getName()),
					positive ? '2' : '4', (int) (perc * 100));
		});
		GuiNode equipNode = this.addEquipButtons((slot, item) -> this.manager.getStands().forEach(i -> i.setItemStackToSlot(slot, item.copy())));
		
		//Space between the nodes
		final int spaceX = (this.overlayWidth-equipNode.getWidth()-settingsNode.getWidth())/3;
		
		//Init's the nodes
		settingsNode.init(this.overlayX+spaceX, this.height/2-settingsNode.getHeight()/2);
		equipNode.init(this.overlayX+spaceX*2+settingsNode.getWidth(), this.height/2-equipNode.getHeight()/2);
	}
	
	/**
	 * Used by {@link #initSingle()}
	 */
	private GuiNode addSettingsButtons(int btnWidth,IUpdate onExec) {
		//Amount of buttons
		final int btnAmount = this.toggleButtons.length;
		//Space between the buttons
		final int btnSpaceY = MathHelper.clamp((this.height - 30 - 20 * btnAmount) / (btnAmount - 1), 0, 14);

		//Size of the node
		final int w = btnWidth+10;
		final int h = btnAmount * (20 + btnSpaceY) + 20;
		
		return new GuiNode(w,h,(x,y)->{			
			// Adds the background
			this.addWidget(new TmBackgroundWidget(x, y, w, h, 0xa0000000).setOutline(0xff000000,1));
			// Adds the info-text
			this.addWidget(new TmTextWidget(x + w/2, y + 10,
					TextUtil.getInstance().getByKey("modules.asedit.gui.settings.settings"), 0xffffffff));
			// For all buttons
			for (int i = 0; i < this.toggleButtons.length; i++) {
				// Gets the toggle button
				AsEditToggleButton toggle = this.toggleButtons[i];
				
				// Adds the button
				this.addWidget(new TmUpdateButton(x+5, y + 20 + (20 + btnSpaceY) * i + btnSpaceY / 2, btnWidth, 20,
						btn -> onExec.execute(toggle, btn != null)));
			}
		});
	}
	
	/**
	 * Adds the equip-buttons
	 */
	private GuiNode addEquipButtons(IEquip onEquip) {
		// Gets the node size
		final int w = 120;
		final int h = 165;

		return new GuiNode(w, h, (x, y) -> {
			// Adds the background
			this.addWidget(new TmBackgroundWidget(x, y, w, h, 0xa0000000).setOutline(0xff000000,1));
			// Adds the info
			this.addWidget(new TmTextWidget(x+w/2, y + 10,TextUtil.getInstance().getByKey("modules.asedit.gui.settings.equip"), 0xffffffff));
			
			// Adds the equip-select button's
			for (int i = 0; i < EnumEditStandEquip.values().length; i++) {
				// Gets the icon
				EnumEditStandEquip slot = EnumEditStandEquip.values()[i];
				// Skips the remove-button
				if (i > 0)
					// Adds the equip-part-selection button
					this.addWidget(
							new TmItemButton(x + w/2 - slot.getOffsetX() - 50, y + h/2 + 20 + slot.getOffsetY(), slot.getIcon(), btn -> {
								// Sets the slot
								this.slot = slot;
								// Updates the GUI
								this.module.guis.update();
							}).setEnabled(this.slot != slot).setTooltip(new StringTextComponent(slot.getName())));
				
				// Adds the equip-selection button
				this.addWidget(
						new TmItemButton(x + w/2 + slot.getOffsetX() + 30, y + h/2 + 20 + slot.getOffsetY(), slot.getIcon(), btn -> {
							// Updates the item
							onEquip.execute(this.slot.getResponse(), slot.getFromSlot(this.minecraft.player).copy());
						}).setTooltip(new StringTextComponent(slot.getCopy())));
			}
		});

	}
	
	/**
	 * Init when one stand is selected
	 */
	private void initSingle() {
		//Gets the nodes
		GuiNode settingsNode = this.addSettingsButtons(95, (btn, set) -> {
			if (set)
				btn.reverse(this.stand);

			// Gets the state
			boolean state = btn.get(this.stand);

			return TextUtil.getInstance().offMultiple(" §" + (state ? '2' : '4'),
					"modules.asedit.gui.settings.settings." + btn.getName(),
					"modules.asedit.gui.settings.settings." + (state ? "on" : "off"));
		});
		GuiNode nameFieldNode = this.addNameField();
		GuiNode equipNode = this.addEquipButtons(this.manager.getSelectedStand()::setItemStackToSlot);

		//Biggest node
		final int big = Math.max(nameFieldNode.getWidth(), equipNode.getWidth());
		
		//Space between the nodes
		final int spaceX = (this.overlayWidth-settingsNode.getWidth()-big)/3;
		final int spaceY = (this.height-nameFieldNode.getHeight()-equipNode.getHeight())/3;
		
		//Adds the nodes
		settingsNode.init(this.overlayX+spaceX, this.height/2-settingsNode.getHeight()/2);
		equipNode.init(this.overlayX+settingsNode.getWidth()+spaceX*2+big/2-equipNode.getWidth()/2, spaceY);
		nameFieldNode.init(this.overlayX+settingsNode.getWidth()+spaceX*2+big/2-nameFieldNode.getWidth()/2, spaceY*2+equipNode.getHeight());
	}
	
	/**
	 * Used by {@link #initSingle()}
	 */
	private GuiNode addNameField() {
		// Size of the name-field
		final int nfW = (int) Math.min(120, this.overlayWidth*.4f);

		// Size of the button
		final int nfBtnW = 60;
		final int nfBtnH = 20;

		//Size of the node
		final int w = nfW + 20;
		final int h = 75;
		
		return new GuiNode(w, h, (x,y)->{			
			// Adds the background
			this.addWidget(new TmBackgroundWidget(x, y, w, h, 0xa0000000).setOutline(0xff000000,1));
			// Adds the text-info
			this.addWidget(new TmTextWidget(x + w/2, y+10,
					TextUtil.getInstance().getByKey("modules.asedit.gui.settings.settings.name"), 0xffffffff));
			// Adds the name-field
			TmTextfield nameField = new TmTextfield(x + 5, y+30, w-10, 15, "",null);
			nameField.setText(this.stand.hasCustomName() ? this.stand.getCustomName().getString() : "");
			nameField.setResponder(name -> this.stand.setCustomName(name.isEmpty() ? null : new StringTextComponent(name)));
			this.addWidget(nameField);
			
			// Adds the name-field button
			this.addWidget(new TmUpdateButton(x + w/2 - nfBtnW/2, y + h - 25, nfBtnW, nfBtnH, btn -> {
				if (btn != null)
					this.stand.setCustomNameVisible(!this.stand.isCustomNameVisible());
				return TextUtil.getInstance().offMultiple(" §" + (this.stand.isCustomNameVisible() ? '2' : '4'),
						"modules.asedit.gui.settings.settings.customnamevisible",
						"modules.asedit.gui.settings.settings." + (this.stand.isCustomNameVisible() ? "on" : "off"));
			}));
		});
	}
	
	@FunctionalInterface
	private interface IEquip {
		public void execute(EquipmentSlotType slot, ItemStack stack);
	}
	
	@FunctionalInterface
	private interface IUpdate {
		public String execute(AsEditToggleButton btn, boolean set);
	}
}

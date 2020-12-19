package de.whiletrue.toolsmod.gui.widgets;

import java.util.Optional;
import java.util.function.Consumer;

import de.whiletrue.toolsmod.gui.widgets.preset.TmWidget;
import de.whiletrue.toolsmod.util.Keybind;

public class TmKeybindButton extends TmButton {

	// Selection executor for the keybind
	private Consumer<Keybind> onSelect;

	//Current keybind
	private Keybind bind;
	
	public TmKeybindButton(int x, int y, int width, int height, Keybind keyBind, Consumer<Keybind> onSelect) {
		super(x, y, width, height, null, null);
		this.onSelect = onSelect;
		// Updates the name
		this.bind=keyBind;
		this.updateDisplay();
	}

	@Override
	public Optional<TmWidget> onMouseClicked(double mX, double mY, int ticks) {
		// Checks if the button is enabled and hovered
		if (this.enabled && this.hovered)
			// Focuses the button and enables the selection
			return Optional.of(this);

		return Optional.empty();
	}

	@Override
	public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
		//Updates the keybind
		this.bind.updateFromEvent(keyCode);
		//Updates the display
		this.updateDisplay();
		//Executes the select event
		this.onSelect.accept(this.bind);
		return true;
	}
	
	@Override
	public boolean onKeyReleased(int keyCode, int scanCode, int modifiers) {
		//Unfocuses the button
		this.unFocuse();
		return true;
	}

	@Override
	protected String getRenderText(boolean focused) {
		return focused ? "> §e"+this.display+"§r <" : super.getRenderText(focused);
	}
	
	/**
	 * Updates the display and gets the new value from the keybind
	 */
	public void updateDisplay() {
		this.display = this.bind.getName();
	}
}

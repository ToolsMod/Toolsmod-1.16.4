package de.whiletrue.toolsmod.gui.widgets;

public class TmUpdateButton extends TmButton{

	public TmUpdateButton(int x, int y, int width, int height, IPressable onPress) {
		super(x, y, width, height, null, null);
		//Sets the click handler
		this.setClickHandler(onPress);
		this.onPress.execute(null);
	}
	
	@Override
	public void onInitUpdate() {
		this.onPress.execute(null);
	}
	
	public TmUpdateButton setClickHandler(IPressable onPress) {
		this.onPress=btn->this.display=onPress.execute(btn==null?null:(TmUpdateButton) btn);
		return this;
	}
	
	@FunctionalInterface
	public interface IPressable{
		public String execute(TmUpdateButton button);
	}
}

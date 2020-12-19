package de.whiletrue.toolsmod.gui.widgets;

public class TmSliderValues<Type> extends TmSlider{

	//Set values
	private Type[] values;
	
	//Currently selected type (Helps to save calculation data)
	private Type selected = null;
	
	public TmSliderValues(int x, int y, int width, int height, Type[] values,int index,
			IValueDragable<Type> onDrag) {
		super(x, y, width, height, 0, values.length-1, index, (tm,val)->"");
		this.values=values;
		
		//Updates the drag handler
		this.setDragHandler(onDrag);
	}
	
	/**
	 * Sets the drag handler
	 * @param onDrag the drag handler
	 */
	public TmSliderValues<Type> setDragHandler(IValueDragable<Type> onDrag){
		//Sets the drag handler
		super.setDragHandler((tm,val)->{
			
			//Gets the type
			Type t = this.values[(int)val];
			
			//Checks if the type is different
			if(t.equals(this.selected))
				return this.display;
			
			//Updates the selected
			this.selected=t;
			
			//Updates the display from the event
			return onDrag.execute(t, (int)val);
		});
		return this;
	}
	
	/**
	 * Sets the slider state to the given value
	 * 
	 * @param value the value
	 */
	public TmSliderValues<Type> setStateByValue(Type value){
		//Iterates over every value
		for(int i=0; i < this.values.length; i++)
			//Checks if the values if equal
			if(value.equals(this.values[i])){
				//Sets the state
				this.setByValue(i);
				break;
			}
		
		return this;
	}
	
	/**
	 * Returns the current selected value
	 */
	public Type getStateValue(){
		return this.selected;
	}

	@FunctionalInterface
	public interface IValueDragable<Type>{
		public String execute(Type value,int index);
	}
}

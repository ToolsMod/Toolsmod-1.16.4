package de.whiletrue.toolsmod.gui.widgets.rounding;

public class GuiNode {
	
	//Width and height of the node
	private int width,height;
	
	//Node init handler
	private INodeInit onInit;
	
	public GuiNode(float width,float height,INodeInit onInit) {
		this.width=(int)width;
		this.height=(int)height;
		this.onInit=onInit;
	}
	
	/**
	 * Init's the node at position @x and @y
	 * @param x the position x
	 * @param y the position y
	 */
	public void init(int x,int y) {
		this.onInit.execute(x, y);
	}
	
	public int getWidth() {
		return this.width;
	}
	public int getHeight() {
		return this.height;
	}
	
	
	@FunctionalInterface
	public interface INodeInit{
		public void execute(int x,int y);
	}
		
}

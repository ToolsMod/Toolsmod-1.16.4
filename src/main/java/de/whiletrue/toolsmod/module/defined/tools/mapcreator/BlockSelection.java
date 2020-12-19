package de.whiletrue.toolsmod.module.defined.tools.mapcreator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;

public class BlockSelection {
	
	//The selected block
	private Block selected;
	
	//All blocks that are available for this color
	private List<Block> availableBlocks = new ArrayList<>();
	
	//The color of the palate
	private Color color;
	
	public BlockSelection(Color color,Block... blocks) {
		//Adds the first block
		this.availableBlocks.addAll(Arrays.asList(blocks));
		//Selects the first block
		this.selected=this.availableBlocks.get(0);
		this.color=color;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public Block getSelected() {
		return this.selected;
	}
	
	public void setSelected(Block selected) {
		this.selected = selected;
	}
	
	public List<Block> getAvailableBlocks() {
		return this.availableBlocks;
	}
}


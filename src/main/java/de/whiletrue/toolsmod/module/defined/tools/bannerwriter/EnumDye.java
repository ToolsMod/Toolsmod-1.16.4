package de.whiletrue.toolsmod.module.defined.tools.bannerwriter;

import de.whiletrue.toolsmod.util.classes.ItemUtil;
import de.whiletrue.toolsmod.util.classes.TextUtil;
import net.minecraft.item.Item;

public enum EnumDye {

	WHITE("white","white",0xffffffff,0),
	ORANGE("orange","orange",0xffF1B800,1),
	MAGENTA("magenta","magenta",0xffDD3FD3,2),
	LIGHT_BLUE("light.blue","light_blue",0xff3DA7FA,3),
	YELLOW("yellow","yellow",0xffe4e42a,4),
	LIME("lime","lime",0xff54fc54,5),
	PINK("pink","pink",0xffFF7BC0,6),
	GRAY("gray","gray",0xff646464,7),
	LIGHT_GRAY("light.gray","light_gray",0xffABABAB,8),
	CYAN("cyan","cyan",0xff0085FF,9),
	PURPLE("purple","purple",0xff8a38ba,10),
	BLUE("blue","blue",0xff0005D2,11),
	BROWN("brown","brown",0xff794521,12),
	GREEN("green","green",0xff126C00,13),
	RED("red","red",0xffff0000,14),
	BLACK("black","black",0xffFFFFFF,15);

	private String gameKey,langKey;
	private int color,id;
	
	private EnumDye(String languageKey,String gameKey,int color,int id) {
		this.gameKey=gameKey;
		this.langKey=languageKey;
		this.color=color;
		this.id=id;
	}
	
	/**
	 * @return the banner with the given color
	 */
	public Item getBanner() {
		return ItemUtil.getInstance().getItemFromName(this.gameKey+"_banner").get();
	}
	
	public String getName() {
		return TextUtil.getInstance().getByKey("global.color."+this.langKey);
	}
	
	public int getColor() {
		return this.color;
	}
	public int getId() {
		return this.id;
	}
	
}

package de.whiletrue.toolsmod.module.defined.tools.bannerwriter;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public enum EnumBannertypes {

	//Format: name (0=forground 1=background)
	EMPTY("",' '),
	LETTER_A("ms0 ls0 rs0 ts0 bo1",'a'),
	LETTER_B("ms0 ts0 bs0 ls0 rs0",'b'),
	LETTER_C("ts0 bs0 ls0",'c'),
	LETTER_D("rs0 ts0 bs0 cbo1 ls0",'d'),
	LETTER_E("ts0 ms0 bs0 ls0",'e'),
	LETTER_F("ts0 ls0 ms0",'f'),
	LETTER_G("rs0 hh1 ls0 bs0 ts0",'g'),
	LETTER_H("ls0 rs0 ms0",'h'),
	LETTER_I("ts0 bs0 cs0",'i'),
	LETTER_J("ls0 hh1 bs0 rs0",'j'),
	LETTER_K("ms0 vhr1 drs0 dls0 ls0",'k'),
	LETTER_L("ls0 bs0",'l'),
	LETTER_M("tt0 tts1 ls0 rs0",'m'),
	LETTER_N("ls0 rs0 drs0",'n'),
	LETTER_O("ts0 bs0 mr1 ls0 rs0",'o'),
	LETTER_P("rs0 hhb1 ls0 ms0 ts0",'p'),
	LETTER_Q("vhr0 vh0 mr1 ls0 rs0 br0",'q'),
	LETTER_R("rs0 hhb1 ts0 ls0 drs0",'r'),
	LETTER_S("ts0 bs0 drs0",'s'),
	LETTER_T("ts0 cs0",'t'),
	LETTER_U("ls0 rs0 bs0",'u'),
	LETTER_V("ls0 dls0",'v'),
	LETTER_W("bt0 bts1 ls0 rs0",'w'),
	LETTER_X("drs0 dls0",'x'),
	LETTER_Y("drs0 vhr1 dls0",'y'),
	LETTER_Z("ts0 bs0 dls0",'z'),
	NUMBER_1("tl0 cs0 cbo1 bs0",'1'),
	NUMBER_2("ts0 mr1 bs0 dls0",'2'),
	NUMBER_3("bs0 ms0 ts0 cbo1 rs0",'3'),
	NUMBER_4("ls0 hhb1 rs0 ms0",'4'),
	NUMBER_5("bs0 mr1 ts0 drs0",'5'),
	NUMBER_6("rs0 hh1 ls0 ms0 bs0",'6'),
	NUMBER_7("dls0 ts0",'7'),
	NUMBER_8("ts0 ls0 ms0 bs0 rs0",'8'),
	NUMBER_9("ls0 hhb1 ms0 ts0 rs0",'9'),
	NUMBER_0("ts0 bs0 ls0 rs0 dls0",'0');
	
	//The nbt tag that must be foramtted (1 = background color 0 = forground color)
	private String formatnbt;
	//Equivalent character
	private char equal;
	
	private EnumBannertypes(String formatnbt,char equal) {
		this.formatnbt=formatnbt;
		this.equal=equal;
	}
	
	public char getEqual() {
		return equal;
	}
	
	public String getFormatnbt() {
		return this.formatnbt;
	}
	
	/**
	 * @return the converted tag from the banner
	 */
	public CompoundNBT getTag(EnumDye forground,EnumDye background,boolean outline) {
		//Gets the nbt and adds the outline if requested
		String nbt = this.getFormatnbt()+(outline?" bo1":"");

		return new CompoundNBT() {{
			this.put("BlockEntityTag", new CompoundNBT() {{
				this.put("Patterns", new ListNBT() {{
					//For every pattern
					for(String pattern : nbt.split(" ")) {
						//Checks if the pattern is set
						if(pattern.trim().isEmpty())
							continue;
						//Gets the pattern name
						String name = pattern.substring(0,pattern.length()-1);
						//Gets the color
						int color = pattern.substring(name.length()).equalsIgnoreCase("0")?forground.getId():background.getId();
						
						//Adds the component
						this.add(new CompoundNBT() {{
							this.putString("Pattern", name);
							this.putInt("Color", color);
						}});
					}
				}});
			}});
		}};
	}
	
	/**
	 * @param equal
	 * @return the bannertype by its equal char
	 */
	public static EnumBannertypes getByChar(char equal) {
		for(EnumBannertypes b:EnumBannertypes.values())
			if(b.getEqual()==equal)
				return b;
		return EMPTY;
	}
}
	


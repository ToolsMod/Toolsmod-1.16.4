package de.whiletrue.toolsmod.util.classes;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;
import java.util.stream.Collectors;

import de.whiletrue.toolsmod.mod.Toolsmod;

public class TextUtil {

    //Instance
    private static TextUtil instance;
    
    //Reference to the game
    private Minecraft mc = Minecraft.getInstance();

    private TextUtil(){}

    public static TextUtil getInstance() {
    	if(instance==null)
    		instance=new TextUtil();
    	return instance;
    }

    /**
     * Returns the formatted prefix
     * */
    public String getPrefix(){
        return String.format("ง8[ง%c%sง8] ง%c", Toolsmod.COLOR_MAIN,Toolsmod.NAME,Toolsmod.COLOR_SECONDARY);
    }

    /**
     * Returns the loaded language item
     *
     * @param key the key
     * */
    public String getByKey(String key){
    	return LanguageMap.getInstance().func_230503_a_(Toolsmod.ID+"."+key);
    }

    /**
     * Returns the formatted text
     * If an error occurs it returns "Invalid-Formatting"
     *
     * @param key the key
     * @param formatting the formatting
     */
    public String getByKey(String key,Object... formatting){
        try {
            return String.format(this.getByKey(key),formatting);
        }catch(Exception e){
            return "Invalid formatted: "+key;
        }
    }
    
    /**
     * Returns the formatted text
     * If an error occurs it returns "Invalid-Formatting"
     * 
     * @param key the key
     * @param formatting the formatting
     */
    public ITextComponent getITextByKey(String key,Object...formatting) {
    	return new StringTextComponent(this.getByKey(key,formatting));
    }

    /**
     * Returns a join of all keys with the splitter
     * @param splitter the splitter
     * @param keys the keys
     * @return the formatted string
     */
    public String offMultiple(String splitter,String... keys) {
    	return Arrays.stream(keys).map(this::getByKey).collect(Collectors.joining(splitter));
    }
    
    /**
     * Returns the loaded language item
     * uses the key and appends either the on or off string depending on the state variable
     *
     * @param key
     * @param state
     * @param on
     * @param off
     * @return
     */
    public String getByKey(String key,boolean state,String on,String off){
        return this.getByKey(key+(state?on:off));
    }

    /**
     * Sends the given message and formats it
     *
     * @param key the message to send
     * @param format the format paramter
     * */
    public void sendMessage(String key,Object... format){
        //Generates the prefix
        String pref = this.getPrefix();

        //Sends the message
        Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(this.appendColors(pref+this.getByKey(key,format)));
    }

    /**
     * Sends the given message with as an error
     *
     * @param key the error message
     * @param format the formatting
     * */
    public void sendError(String key,Object... format){
        //Generates the prefix
        String pref = String.format("ง8[ง%c%sง8] งc", Toolsmod.COLOR_MAIN,Toolsmod.NAME);

        //Sends the message
        this.mc.ingameGUI.getChatGUI().printChatMessage(this.appendColors(pref+this.getByKey(key,format)));
    }

    /**
     * Sends the given message without formatting
     *
     * @param message the formatted message
     * */
    public void sendMessageRaw(String message){
        //Generates the prefix
        String pref = String.format("ง8[ง%c%sง8] ง%c", Toolsmod.COLOR_MAIN,Toolsmod.NAME,Toolsmod.COLOR_SECONDARY);

        //Sends the message
        this.mc.ingameGUI.getChatGUI().printChatMessage(this.appendColors(pref+message));
    }

    /**
     * Sends an emtpy line
     * */
    public void sendEmptyLine(){
        Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(new StringTextComponent(""));
    }

    /**
     * Applies the give styles to the text and returns the text-component
     *
     * @param text the text to format
     * */
    public TextComponent appendColors(String text){
        //Final text component
        TextComponent fin = new StringTextComponent("");

        //Current text component
        TextComponent current = new StringTextComponent("");

        //Splits the text
        String split[] = text.split("ยง");

        //Iterates over every split
        for(String s:split){
            //Checks if the string has a style character
            if(s.length()<=0)
                continue;

            //Gets the style character
            char style = s.charAt(0);

            //Gets the text-formatting
            TextFormatting tf = TextFormatting.fromFormattingCode(style);

            //Checks if the style is invalid
            if(tf==null){
                //Just appends the text to the component
                current.append(new StringTextComponent(s));
                //Appends the component to the final
                fin.append(current);
                //Resets the current
                current=new StringTextComponent("");
                continue;
            }

            //Gets the current color
            Color color = current.getStyle().getColor();

            //Appends the component
            fin.append(current);
            //Resets the current component
            current=new StringTextComponent(s.substring(1,s.length()));
            //Checks if the style is a color
            if(!tf.isColor())
                //Sets the previous color
            	current.getStyle().setColor(color);
            //Sets the new color
            current.getStyle().setFormatting(tf);
        }

        //Appends the last text
        fin.append(current);

        //Returns the final component
        return fin;
    }
    
    /**
	 * Splits the @text every time the @minWidth is reached and does a force split once @maxwidth is reached
	 * @param text the text to split
	 * @param minWidth the width where a split can start
	 * @param maxWidth the absolute maxwidth where a force split will be done
	 * @return the splitted text with \n between
	 */
	public String splitStringOnWidth(String text,float minWidth,float maxWidth) {
		//Current frame
		String split = "";
		
		//Full text with the newlines
		String full="";
		
		//Iterates over every character
		for(char c : text.toCharArray()) {
			//Gets the current text width
			int tw = this.mc.fontRenderer.getStringWidth(split);
			
			//Checks if the min-width was reached and eighter an space or the max width got reached
			if(tw > minWidth && (c == ' ' || tw > maxWidth)) {
				//Splits the text and appends it
				full+="\n"+split;
				//Checks if the new valid isn't a space
				if(c!=' ')
					split=String.valueOf(c);
				else
					split="";
			}else
				//Appends the next character
				split+=c;
		}
		
		//Appends the last bit
		full+="\n"+split;
		
		if(full.length()>0)
			full=full.substring(1);
		
		//Returns the split text
		return full;
	}
	
	/**
	 * Trims a text to the given width and appends the @append
	 * @param text the text to trim
	 * @param width the width to trim to
	 * @param append the append
	 * @return the shorted text
	 */
	public String trimStringToWidth(String text,int width,String append) {
		//The converted text
		String convert = "";
		
		//Iterates over all characters
		for(char c : text.toCharArray()) {
			//Checks if another character fits
			if(this.mc.fontRenderer.getStringWidth(convert+c) <= width)
				//Appends the next
				convert+=c;
			else
				//Returns with the append
				return convert+append;
		}
		
		return text;
	}
}

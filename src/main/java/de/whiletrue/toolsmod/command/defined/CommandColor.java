package de.whiletrue.toolsmod.command.defined;

import java.awt.Color;

import de.whiletrue.toolsmod.command.ArgsReturn;
import de.whiletrue.toolsmod.command.Arguments;
import de.whiletrue.toolsmod.command.CommandError;
import de.whiletrue.toolsmod.util.classes.ItemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class CommandColor extends Command{

	//All allowed items
	private Item[] allowed = {
		Items.LEATHER_BOOTS,
		Items.LEATHER_CHESTPLATE,
		Items.LEATHER_LEGGINGS,
		Items.LEATHER_HELMET,
	};
	
	public CommandColor() {
		super("Color");
	}

	@Override
	public CommandError execute(Arguments args) {
		 //Checks if the player is in creative mode
        if(Minecraft.getInstance().playerController.isNotCreative())
            return CommandError.of("command.color.error.gm");
		
		//Gets the color
		ArgsReturn<Color> optColor = args.nextColor();
		
		//If not given
		if(optColor.hasArgumentError())
			return this.getSyntaxError();
		
		//If not parse-able
		if(optColor.hasParseError())
			return CommandError.of("command.color.error.parse", optColor.getAsString());
		
		//Gets the item
		ItemStack hand = Minecraft.getInstance().player.getHeldItemMainhand();
		
		//Checks if the item is valid
		x:{
			for(Item a : this.allowed)
				if(hand.getItem().equals(a))
					break x;
			return CommandError.of("command.color.error.item");
		}
		
		//Sets the color
		DyeableArmorItem f = (DyeableArmorItem) hand.getItem();
		f.setColor(hand, optColor.get().getRGB());
		
		//Sends the update
		ItemUtil.getInstance().setItem(hand);
		
		return CommandError.none();
	}
}

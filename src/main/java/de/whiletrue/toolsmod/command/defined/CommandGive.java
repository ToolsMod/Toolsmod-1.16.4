package de.whiletrue.toolsmod.command.defined;

import de.whiletrue.toolsmod.command.ArgsReturn;
import de.whiletrue.toolsmod.command.Arguments;
import de.whiletrue.toolsmod.command.CommandError;
import de.whiletrue.toolsmod.util.classes.ItemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class CommandGive extends Command{

	public CommandGive() {
		super("Give","get","set");
	}

	@Override
	public CommandError execute(Arguments args) {
		 //Checks if the players gamemode is set to creative
        if(!Minecraft.getInstance().player.isCreative())
            return CommandError.of("command.give.error.gm");

        //Gets an item
        ArgsReturn<Item> optItm = args.nextItem();
        //Checks if the item is given
        if(optItm.hasArgumentError())
        	return this.getSyntaxError();

        //Checks if the item could not be found
        if(optItm.hasParseError())
            return CommandError.of("command.give.error.item");

        ArgsReturn<Integer> optAmount = args.nextInteger();
        //Checks if the amount could not be parsed
        if(optAmount.hasParseError())
            return CommandError.of("command.give.error.amount");

        ArgsReturn<CompoundNBT> optNbt = args.nextNbt();
        //Checks if the nbt could not be parsed
        if(optNbt.hasParseError())
            return CommandError.of("command.give.error.nbt");

        //Sets nbt and amount with either the given or default value
        int amount = optAmount.isPresent()?optAmount.get():1;
        CompoundNBT nbt = optNbt.isPresent()?optNbt.get():new CompoundNBT();

        //Creates the item from the given parameters
        ItemStack fin = new ItemStack(optItm.get(),amount);
        fin.setTag(nbt);

        //Gives the item to the player
        ItemUtil.getInstance().setItem(fin);

        //Exits with not errors
        return CommandError.none();
	}

}

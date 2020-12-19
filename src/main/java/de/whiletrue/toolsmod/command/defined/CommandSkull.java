package de.whiletrue.toolsmod.command.defined;

import de.whiletrue.toolsmod.command.ArgsReturn;
import de.whiletrue.toolsmod.command.Arguments;
import de.whiletrue.toolsmod.command.CommandError;
import de.whiletrue.toolsmod.util.classes.ItemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;

public class CommandSkull extends Command{

	public CommandSkull() {
		super("Skull", "head");
	}

	@Override
	public CommandError execute(Arguments args) {
		//Checks if the user is in gamemode creative
        if(Minecraft.getInstance().playerController.isNotCreative())
            return CommandError.of("command.skull.error.gm");

        //Gets the skull-owner
        ArgsReturn<String> optName = args.nextString();

        //Checks if the skull got parsed
        if(optName.hasArgumentError())
            return this.getSyntaxError();

        //Creates the nbt compound
        CompoundNBT nbt = new CompoundNBT();
        //Sets the skull-owner tag to the given name
        nbt.putString("SkullOwner", optName.get());

        //Creates the item and sets the tag
        ItemStack item = new ItemStack(Items.PLAYER_HEAD,1);
        item.setTag(nbt);

        //Gives the item to the player
        ItemUtil.getInstance().setItem(item);

        //Exits without an error
        return CommandError.none();
	}

}

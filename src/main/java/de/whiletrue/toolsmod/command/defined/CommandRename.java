package de.whiletrue.toolsmod.command.defined;

import de.whiletrue.toolsmod.command.ArgsReturn;
import de.whiletrue.toolsmod.command.Arguments;
import de.whiletrue.toolsmod.command.CommandError;
import de.whiletrue.toolsmod.util.classes.ItemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;

public class CommandRename extends Command{

	public CommandRename() {
		super("Rename");
	}

	@Override
	public CommandError execute(Arguments args) {
		//Checks if the player is in creative mode
        if(Minecraft.getInstance().playerController.isNotCreative())
            return CommandError.of("command.rename.error.gm");
		
        //Gets the name
        ArgsReturn<String> name = args.nextStringleft();
        
        //If not given
        if(name.hasArgumentError())
        	return this.getSyntaxError();
		
        //Gets the item
        ItemStack hand = Minecraft.getInstance().player.getHeldItemMainhand();
        
        //Checks if the item is valid
        if(hand.getItem().equals(Items.AIR))
        	return CommandError.of("command.rename.error.item");
        
        //Renames the item
        hand.setDisplayName(new StringTextComponent(name.get()));
        //Sends the update
        ItemUtil.getInstance().setItem(hand);
        
		return CommandError.none();
	}

}

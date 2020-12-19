package de.whiletrue.toolsmod.command.defined;

import java.util.stream.Collectors;

import de.whiletrue.toolsmod.command.ArgsReturn;
import de.whiletrue.toolsmod.command.Arguments;
import de.whiletrue.toolsmod.command.CommandError;
import de.whiletrue.toolsmod.util.classes.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;

public class CommandNBT extends Command{

    //Available arguments
    private String[] allowedArgs = {"copy","copyraw","tree"};
	
	public CommandNBT() {
		super("NBT");
	}

	@Override
	public CommandError execute(Arguments args) {
		//Gets the item the player is holding
        ItemStack heldItem = Minecraft.getInstance().player.getHeldItemMainhand();

        //Checks if the player is holding an item
        if(heldItem.getItem().equals(Items.AIR))
            return CommandError.of("command.nbt.error.item");

        //Checks if that item has a tag
        if(!heldItem.hasTag())
            return CommandError.of("command.nbt.error.nbt");

        //Gets the tag
        CompoundNBT nbt = heldItem.getTag();

        //Gets the action
        ArgsReturn<String> optAction = args.nextOf(this.allowedArgs);

        //Checks if the action is given
        if(optAction.hasArgumentError()){
            //Displays that nbt
            TextUtil.getInstance().sendMessageRaw(nbt.toString().replace((char)167, '&'));
            return CommandError.none();
        }

        //Checks if the action could be verified
        if(optAction.hasParseError())
            return this.getSyntaxError();

        //Gets the action
        String action = optAction.get();

        //Checks if the action is to copy
        if(action.equalsIgnoreCase(this.allowedArgs[0])){
            //Copies the string but replaces the paragraph characters with and characters
            Minecraft.getInstance().keyboardListener.setClipboardString(nbt.toString().replace((char)167, '&'));
            return CommandError.none();
        }

        //Checks if the action is copy-raw
        if(action.equalsIgnoreCase(this.allowedArgs[1])){
            //Copies the string raw
            Minecraft.getInstance().keyboardListener.setClipboardString(nbt.toString());
            return CommandError.none();
        }

        if(action.equalsIgnoreCase(this.allowedArgs[2]))
            //Sends the tree-view
            TextUtil.getInstance().sendMessage("command.nbt.tree",this.appendTag(nbt,0));

        //Exits with no errors
        return CommandError.none();
	}
	
	private String appendTag(INBT nbt,int depth){
        //Gets the depth string
        String depthString = new String(new char[depth]).replace("\0", "    ");

        //Checks for nbt-list
        if(nbt instanceof ListNBT){
            //Gets the list
            ListNBT list = (ListNBT) nbt;
            //Gets all items from the list
            return list.stream()
                    //Maps them to there equivalent
                    .map(i->this.appendTag(i,depth))
                    //Gets them
                    .collect(Collectors.joining());
        }

        //Checks for nbt-compound
        if(nbt instanceof CompoundNBT){
            //Gets the compound
            CompoundNBT comp = (CompoundNBT) nbt;
            //Gets all keys
            return comp.keySet().stream()
                    //Maps them to there equivalents
                    .map(i->String.format("%s: %s",i,this.appendTag(comp.get(i),depth+1)))
                    //Gets them
                    .collect(Collectors.joining("\n"+depthString,"\n"+depthString,""));
        }

        //Just returns the string
        return nbt.toString();
    }

}

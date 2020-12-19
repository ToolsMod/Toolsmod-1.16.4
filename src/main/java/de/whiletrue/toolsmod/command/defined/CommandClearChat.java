package de.whiletrue.toolsmod.command.defined;

import de.whiletrue.toolsmod.command.ArgsReturn;
import de.whiletrue.toolsmod.command.Arguments;
import de.whiletrue.toolsmod.command.CommandError;
import net.minecraft.client.Minecraft;

public class CommandClearChat extends Command{

	public CommandClearChat() {
		super("ClearChat", "cc","chatclear");
	}

	@Override
	public CommandError execute(Arguments args) {
		//Gets the optional history clear
        ArgsReturn<String> optHistory = args.nextString();

        //Checks if the history parameter got parsed
        if(!optHistory.hasArgumentError() && !optHistory.get().equalsIgnoreCase("history"))
            return CommandError.of("command.clearchat.error",optHistory.getAsString());

        //Clears all chat messages
        Minecraft.getInstance().ingameGUI.getChatGUI().clearChatMessages(optHistory.isPresent());

        //Exits with no errors
        return CommandError.none();
	}

}

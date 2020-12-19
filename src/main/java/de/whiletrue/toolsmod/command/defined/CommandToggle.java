package de.whiletrue.toolsmod.command.defined;

import de.whiletrue.toolsmod.command.ArgsReturn;
import de.whiletrue.toolsmod.command.Arguments;
import de.whiletrue.toolsmod.command.CommandError;
import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.util.classes.TextUtil;

public class CommandToggle extends Command {
    public CommandToggle() {
        super("Toggle", "t");
    }

    @Override
    public CommandError execute(Arguments args) {

        //Gets the module
        ArgsReturn<Module> optMod = args.nextModule();

        //Checks if the argument was given
        if(optMod.hasArgumentError())
            return CommandError.of("command.toggle.error.given.module");

        //Checks if the module exists
        if(optMod.hasParseError())
            return CommandError.of("command.toggle.error.invalid.module",optMod.getAsString());

        //Gets the module
        Module mod = optMod.get();

        //Checks if the module is allowed
        if(!mod.isAllowed())
        	return CommandError.of("command.toggle.error.allowed", mod.getName());
        
        //Gets the state
        boolean state = mod.isActive();

        //Toggles the module
        boolean notConflicting = mod.toggle();

        //If any conflicts happened, stop
        if(notConflicting) {
        	//Sends the chat message
        	TextUtil.getInstance().sendMessage("command.toggle.error.toggle."+(!state?"on":"off"),mod.getName());

	    	//Saves the settings
	    	Toolsmod.getInstance().getModuleManager().save();
        }
        //Exits without an error
        return CommandError.none();
    }
}

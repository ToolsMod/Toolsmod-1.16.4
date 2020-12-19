package de.whiletrue.toolsmod.command.defined;

import de.whiletrue.toolsmod.command.ArgsReturn;
import de.whiletrue.toolsmod.command.Arguments;
import de.whiletrue.toolsmod.command.CommandError;
import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.util.classes.TextUtil;

public class CommandBind extends Command{

	public CommandBind() {
		super("Bind");
	}

	 @Override
	    public CommandError execute(Arguments args) {
	        //Gets the module
	        ArgsReturn<Module> optMod = args.nextModule();

	        //Checks if the module got parsed
	        if(optMod.hasArgumentError())
	            return this.getSyntaxError();

	        //Checks if the module was not found
	        if(optMod.hasParseError())
	            return CommandError.of("command.bind.error.parse.module",optMod.getAsString());

	        //Checks if the mod is allowed
	        if(!optMod.get().isAllowed())
	        	return CommandError.of("command.bind.error.allowed", optMod.get().getName());
	        	
	        //Gets the new keyBind
	        ArgsReturn<Integer> optKey = args.nextKeyCode();

	        //Checks if the keyBind got parsed
	        if(optKey.hasArgumentError())
	            return CommandError.of("command.bind.error.given.keycode");

	        //Checks if the keyBind could be parsed
	        if(optKey.hasParseError())
	            return CommandError.of("command.bind.error.parse.keycode",optKey.getAsString());

	        //Sets the new keybind
	        optMod.get().setKeyBind(optKey.get());

	        //Sends the info message
	        TextUtil.getInstance().sendMessage("command.bind.info.success",optMod.get().getName(),optKey.getAsString());

	        //Saves the bind
	    	Toolsmod.getInstance().getModuleManager().save();
	        
	        return CommandError.none();
	    }
	
}

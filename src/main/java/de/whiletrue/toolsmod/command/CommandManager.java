package de.whiletrue.toolsmod.command;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonObject;

import de.whiletrue.toolsmod.command.defined.Command;
import de.whiletrue.toolsmod.command.defined.CommandAsSave;
import de.whiletrue.toolsmod.command.defined.CommandBind;
import de.whiletrue.toolsmod.command.defined.CommandClearChat;
import de.whiletrue.toolsmod.command.defined.CommandColor;
import de.whiletrue.toolsmod.command.defined.CommandGive;
import de.whiletrue.toolsmod.command.defined.CommandHelp;
import de.whiletrue.toolsmod.command.defined.CommandNBT;
import de.whiletrue.toolsmod.command.defined.CommandProfile;
import de.whiletrue.toolsmod.command.defined.CommandRename;
import de.whiletrue.toolsmod.command.defined.CommandSkull;
import de.whiletrue.toolsmod.command.defined.CommandToggle;
import de.whiletrue.toolsmod.command.defined.CommandYClip;
import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.util.classes.FileUtil;
import de.whiletrue.toolsmod.util.classes.TextUtil;

public class CommandManager {

	//Save file location
	private final File saveFile;
	
    //List with all modules
    private List<Command> registeredCommands;
    
    public CommandManager(String saveFile) {
    	this.saveFile=new File(Toolsmod.ID+'/'+saveFile);
    	
    	//Registers all commands
    	this.registeredCommands = Arrays.asList(
    		new CommandToggle(),
    		new CommandBind(),
    		new CommandAsSave(),
    		new CommandClearChat(),
    		new CommandColor(),
    		new CommandGive(),
    		new CommandHelp(),
    		new CommandNBT(),
    		new CommandProfile(),
    		new CommandRename(),
    		new CommandSkull(),
    		new CommandYClip()
    	);
    	
    	//Loads the command settings
    	this.load();
	}
    
    /**
     * Executes when the player send a message with the client-command indicator
     * */
    public void execute(String command){
        //Splits the command
        String[] args = command.split(" ");

        //Gets the command
        String cmd = args[0].substring(1);

        //Removes the command from the arguments
        args= Arrays.copyOfRange(args,1,args.length);

        //Searches the command
        Optional<Command> optCmd = this.registeredCommands.stream().filter(i->{
            //Checks if the command's name matches or if he has a shortcut that matches
            return i.getName().equalsIgnoreCase(cmd) || Arrays.stream(i.getShortscuts()).filter(j->j.equalsIgnoreCase(cmd)).count()>=1;
        }).findAny();

        //Checks if the command got found
        if(!optCmd.isPresent()){
            //Sends an error
            TextUtil.getInstance().sendError("global.error.command.invalid", Toolsmod.COMMAND_INDICATOR);
            return;
        }
        
        //Checks if the command is allowed
        if(!optCmd.get().isAllowed()) {
        	TextUtil.getInstance().sendError("global.error.command.forbidden");
        	return;
        }

        //Executes the command
        CommandError optError = optCmd.get().execute(Arguments.of(args));

        //Checks if the command had an error
        if(!optError.isFine())
            //Sends the error
            TextUtil.getInstance().sendError(optError.getKey(),optError.getItems());
    }
    
    /**
	 * Saves all modules with their settings
	 */
	public void save() {
		// Save object
		JsonObject save = new JsonObject();

		// For every command
		for (Command cmd : this.registeredCommands)
			//Appends the command setting
			save.addProperty(cmd.getName(), cmd.isAllowed());

		// Saves the object
		FileUtil.getInstance().printToFile(this.saveFile.getAbsolutePath(), save);
	}

	/**
	 * Loads all modules with their settings
	 */
	public void load() {
		try {
			// Gets the content
			JsonObject saved = FileUtil.getInstance().loadFileAsJson(this.saveFile.getAbsolutePath()).get()
					.getAsJsonObject();

			//Iterates over all commands
			for(Command cmd : this.registeredCommands) {
				//Checks if the save has infos about the command
				if(saved.has(cmd.getName()))
					//Tries to update the setting
					try {
						cmd.setAllowed(saved.get(cmd.getName()).getAsBoolean());
					}catch(Exception e) {}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			// If anything went wrong, save the current configuration
			this.save();
		}
	}

    public List<Command> getCommands() {
        return this.registeredCommands;
    }
}

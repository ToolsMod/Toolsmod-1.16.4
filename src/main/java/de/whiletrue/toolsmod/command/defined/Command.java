package de.whiletrue.toolsmod.command.defined;

import de.whiletrue.toolsmod.command.Arguments;
import de.whiletrue.toolsmod.command.CommandError;
import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.util.classes.TextUtil;

public abstract class Command {

    //Name for the execution
    private final String name,
    //Other shortcuts
    shortscuts[];

    //If the command is allowed
    private boolean allowed = true;
    
    public Command(String name,String... shortcuts){
        this.name=name;
        this.shortscuts=shortcuts;
    }

    /**
     * Called when the player wants to execute the command
     *
     * @param args the given arguments
     *
     * @return optional error if empty all worked fine
     * */
    public abstract CommandError execute(Arguments args);

    /**
     * Returns the command-error with a syntax error
     * */
    protected CommandError getSyntaxError(){
        return CommandError.of("global.error.command.syntax",Toolsmod.COMMAND_INDICATOR,this.getName().toLowerCase(),this.getSyntax());
    }

    public String getName() {
        return this.name;
    }

    public String getSyntax() {
        return TextUtil.getInstance().getByKey("command."+this.getName().toLowerCase()+".syntax");
    }

    public String[] getShortscuts() {
        return this.shortscuts;
    }
    
    public void setAllowed(boolean allowed) {
		this.allowed = allowed;
	}
    
    public boolean isAllowed() {
		return this.allowed;
	}
}

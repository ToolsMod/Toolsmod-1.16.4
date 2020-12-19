package de.whiletrue.toolsmod.command.defined;

import java.util.List;

import de.whiletrue.toolsmod.command.ArgsReturn;
import de.whiletrue.toolsmod.command.Arguments;
import de.whiletrue.toolsmod.command.CommandError;
import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.util.classes.TextUtil;

public class CommandHelp extends Command{

    //Amount of command that should be shown per page
    private final int CMDS_PER_PAGE = 5;
	
	public CommandHelp() {
		super("Help", "man");
	}

	@Override
	public CommandError execute(Arguments args) {
		 //Gets the page
        ArgsReturn<Integer> page = args.nextInteger();

        //Checks if a page got parsed
        if(page.hasArgumentError()){
            //Shows the default help-page
            this.showHelpPage(1);
            //Exits with no error
            return CommandError.none();
        }

        //Checks if the page could be parsed
        if(!page.hasParseError()){
            //Shows the page
            this.showHelpPage(page.get());
            return CommandError.none();
        }

        //Gets the module
        ArgsReturn<Module> mod = args.nextModule();

        //Checks if the module could be parsed
        if(mod.hasParseError())
            return CommandError.of("command.help.error.module",mod.getAsString());

        //Shows the module-page
        this.showModuleInfo(mod.get());

        return CommandError.none();
	}
	
	/**
     * Sends the info-page for the given module
     *
     * @param mod the module
     * */
    public void showModuleInfo(Module mod){
        //Shows the help page for that module
        TextUtil.getInstance().sendEmptyLine();//Wrapper
        TextUtil.getInstance().sendMessage("command.help.mod.wrapper",Toolsmod.COLOR_MAIN);//Wrapper
        {
        	TextUtil.getInstance().sendMessage("command.help.mod", mod.getName(),Toolsmod.getInstance().getModuleManager().getModules().size());//Sends the header
        	TextUtil.getInstance().sendMessageRaw("§"+Toolsmod.COLOR_SECONDARY+mod.getDescription());//Module description        	
        }
        TextUtil.getInstance().sendMessage("command.help.mod.wrapper",Toolsmod.COLOR_MAIN);//Wrapper
    }

    /**
     * Sends the command page
     *
     * @param page which page
     * */
    public void showHelpPage(int page){
        //Creates an instance of all commands
        List<Command> cmds = Toolsmod.getInstance().getCommandManager().getCommands();
        //Reduces the page variable by one
        page--;

        //Checks if the current amount of commands works perfectly fine with the given amount of commands per page
        boolean work = cmds.size()%this.CMDS_PER_PAGE==0;

        //Gets the pages of all pages
        int pages = cmds.size()/this.CMDS_PER_PAGE+(work?0:1);

        //Checks if the given page is bigger or lower than the pages
        if(page>=pages||page<0) {
            TextUtil.getInstance().sendError("command.help.error.page",page);
            return;
        }

        //Sends the informations
        TextUtil.getInstance().sendEmptyLine();//Wrapper
        TextUtil.getInstance().sendMessage("command.help.page.wrapper",Toolsmod.COLOR_MAIN);//Wrapper
        {        	
        	TextUtil.getInstance().sendMessage("command.help.page", page+1,pages,cmds.size());//Help page
        	//Goes through all commands that should be included in the current page and displays dem
        	for(int i = page*this.CMDS_PER_PAGE; i < Math.min(cmds.size(),page*this.CMDS_PER_PAGE+this.CMDS_PER_PAGE); i++) {
        		//Gets the name and syntax
        		String name = cmds.get(i).getName();
        		String syntax = cmds.get(i).getSyntax();
        		
        		//Sends the information
        		TextUtil.getInstance().sendMessageRaw(name+" "+syntax);
        	}
        }
        TextUtil.getInstance().sendMessage("command.help.page.wrapper",Toolsmod.COLOR_MAIN);//Wrapper

    }

}

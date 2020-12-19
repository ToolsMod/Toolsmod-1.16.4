package de.whiletrue.toolsmod.command.defined;

import java.util.Optional;

import de.whiletrue.toolsmod.command.ArgsReturn;
import de.whiletrue.toolsmod.command.Arguments;
import de.whiletrue.toolsmod.command.CommandError;
import de.whiletrue.toolsmod.util.PlayerProfile;
import de.whiletrue.toolsmod.util.classes.MojangUtils;
import de.whiletrue.toolsmod.util.classes.TextUtil;

public class CommandProfile extends Command{

    //Thread to search for the user
    private Thread searchThread;
	
	public CommandProfile() {
		super("Profile", "search","find","history");
	}

	@Override
	public CommandError execute(Arguments args) {
		 //Gets the player
        ArgsReturn<String> optSearch = args.nextString();

        //Checks if the search was not given
        if(optSearch.hasArgumentError())
            return this.getSyntaxError();

        //Searches for the player
        this.searchFor(optSearch.get());

        //Exits without an error
        return CommandError.none();
	}

	/**
     * Searches for the given user profile on a separated thread
     * */
    private void searchFor(String user){
        //Checks if the thread is still going
        if(this.searchThread!=null){
            //Kills the thread
            this.searchThread.interrupt();
            this.searchThread=null;
            //Sends the info message
            TextUtil.getInstance().sendMessage("command.profile.info");
        }

        //Creates the thread
        this.searchThread = new Thread(()->{
            //Try's to get the profile
            Optional<PlayerProfile> optionalProfile = MojangUtils.getInstance().getPlayerProfile(user);
            //If he cant get the history if stops
            if(!optionalProfile.isPresent()) {
                //Sends the error
                TextUtil.getInstance().sendError("command.profile.error.search",user);
                //Kills the thread
                this.searchThread=null;
                return;
            }
            //Gets the profile
            PlayerProfile prof = optionalProfile.get();

            //Sends the info
            TextUtil.getInstance().sendMessage("command.profile.wrapper");
            {            	
            	TextUtil.getInstance().sendMessage("command.profile.info.name",prof.getName());
            	TextUtil.getInstance().sendMessage("command.profile.info.uuid",prof.getUuid());
            	TextUtil.getInstance().sendEmptyLine();
            	//Loops through all previous player names
            	for(PlayerProfile.Name nms : prof.getPreviousNames()) {
            		Optional<String> date = nms.formattedDate();
            		//Checks if the date is given
            		if(date.isPresent())
            			TextUtil.getInstance().sendMessageRaw(String.format("%s | %s",nms.getName(),date.get()));
            		else
            			TextUtil.getInstance().sendMessageRaw(nms.getName());
            	}
            }
            this.searchThread = null;
        });
        //Starts the search
        this.searchThread.start();
    }
	
}

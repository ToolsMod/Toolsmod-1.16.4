package de.whiletrue.toolsmod.mod;

import org.apache.commons.lang3.tuple.Pair;

import de.whiletrue.toolsmod.command.CommandManager;
import de.whiletrue.toolsmod.module.ModuleManager;
import de.whiletrue.toolsmod.update.UpdateChecker;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.FMLNetworkConstants;

@Mod(Toolsmod.ID)
public class Toolsmod {
	
	//Mod settings
	public static final String NAME = "Toolsmod";
	public static final String ID = "toolsmod";
	
	//Mod main colors
    public static final char COLOR_MAIN = 'b',
                             COLOR_SECONDARY = '6';
	
    //Mod command indicator
    public static final char COMMAND_INDICATOR = '#';
    
    //Mod version
    public static final float MOD_VERSION = 0.4f;
    
	//Mod instance
	private static Toolsmod instance;
	
	//Module manager
	private ModuleManager moduleManager;
	
	//Command manager
	private CommandManager commandManager;
	
	//Settings manager
	private SettingsManager settingsManager;
	
	//Updater
	private UpdateChecker updater;
	
    public Toolsmod() {
    	//Checks that the mod only loads the on the client side
    	if(FMLEnvironment.dist == Dist.CLIENT)
    		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
    }
    
    /**
     * Handles the init of the mod
     */
    public void init(FMLClientSetupEvent setup) {
    	instance = this;
    	
    	//Registers the mod as client side only
    	ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    	
    	//Registers the event listener
        MinecraftForge.EVENT_BUS.register(new Events());
        
        //Loads the mod settings
        this.settingsManager = new SettingsManager("settings.json");
        
        //Creates the module manager
        this.moduleManager=new ModuleManager("modulesettings.json");
        
        //Creates the command manager
        this.commandManager=new CommandManager("commandsettings.json");
        
        //Loads all settings
        this.moduleManager.load();
        
        //Checks the remote version
        this.updater=new UpdateChecker();
    }

    public static Toolsmod getInstance() {
		return instance;
	}
    public ModuleManager getModuleManager() {
		return this.moduleManager;
	}
    public CommandManager getCommandManager() {
		return this.commandManager;
	}
    public SettingsManager getSettingsManager() {
		return this.settingsManager;
	}
    public UpdateChecker getUpdater() {
		return this.updater;
	}
}

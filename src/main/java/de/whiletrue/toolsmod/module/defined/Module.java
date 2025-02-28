package de.whiletrue.toolsmod.module.defined;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import de.whiletrue.toolsmod.gui.GuiGroup;
import de.whiletrue.toolsmod.gui.config.sub.modules.GuiDefaultSettings;
import de.whiletrue.toolsmod.gui.quickaccess.GuiQuickAccess;
import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.module.ModuleCategory;
import de.whiletrue.toolsmod.settings.Setting;
import de.whiletrue.toolsmod.util.classes.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.event.world.WorldEvent;

public abstract class Module {

	//If the module is allowed with the current setting;
	private boolean allowed=true;
	
    //Module name
    private final String name;
    //Category
    private final ModuleCategory category;
    //Module keyBind
    private int keyBind=-1;
    //If the module is active
    private boolean active;

    //If the module is available at start
    private boolean avaiableAtStart;

    //List with all setting of the module (Will be scanned at runtime)
    private Set<Setting<?>> settings;
    
    //Reference to the game instance
    protected Minecraft mc = Minecraft.getInstance();

    //The quick access gui of the module
    @Nullable
    private GuiGroup<GuiQuickAccess> quickAccessGui;
    
    /**
     * @param name the modules name
     * @param category the modules category
     * */
	public Module(String name, ModuleCategory category,boolean avaiableAtStart) {
    	this.name=name;
        this.category=category;
        this.keyBind=-1;
        this.avaiableAtStart=avaiableAtStart;
    }

	/**
	 * Post creation init function that gets called, when the module gets loaded
	 */
	public void init() {
		//Scans for the module's settings
		this.settings = Arrays.stream(this.getClass().getDeclaredFields())
				//Checks if the field is a setting
				.filter(i->Setting.class.isAssignableFrom(i.getType()))
				//Gets the value
				.map(i->{
					try {
						i.setAccessible(true);
						return (Setting<?>)i.get(this);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
						return null;
					}	
				})
				//Collects all settings
				.collect(Collectors.toSet());
		
		//Checks if the current module has settings
		if(!this.settings.isEmpty())
			//Creates the autogenerated-gui
			this.quickAccessGui=GuiGroup.of(new GuiDefaultSettings(this));
	}
	
    /**
     * Executes when the module gets activated
     * */
    public void onEnable(){}

    /**
     * Executes when the module gets deactivated
     * */
    public void onDisable(){}

    /**
     * Execute when the player disconnects from the server
     * 
     * @param event the disconnect event
     */
    public void onDisconnect(ClientPlayerNetworkEvent.LoggedOutEvent event) {}
    
    /**
     * Executes when the player presses a key
     *
     * @param event the keyType event
     * */
    public void onKeyToggled(KeyInputEvent event){}

    /**
     * Executes when the player places an block
     * 
     * @param event the place event
     */
    public void onPlace(EntityPlaceEvent event) {}
    
    /**
     * Executes every tick
     *
     * @param event the tick event
     * */
    public void onTick(TickEvent.ClientTickEvent event){}
    
    /**
     * Executes when a server-packet get's received
     *
     * @param event the server-packet event
     * */
    public boolean onServerPacket(IPacket<IClientPlayNetHandler> packet){
    	return true;
    }

    /**
     * Executes when a client-packet get's received
     *
     * @param event the client-packet event
     * */
    public boolean onClientPacket(IPacket<IServerPlayNetHandler> packet){
    	return true;
    }

    /**
     * Executes when the render-event gets executed
     *
     * @param event the render-event
     */
    public void onRender2D(boolean tablist,RenderGameOverlayEvent.Pre event){}

    /**
     * Executes when the render-event gets executed
     *
     * @param event the render-event
     */
    public void onRender3D(RenderWorldLastEvent event){}
    
    /**
     * Executes when the user scroll with the mouse
     *
     * @param event the scroll event
     */
    public void onScrollMouse(MouseScrollEvent event){}

    /**
     * Executes when the world loads
     * 
     * @param evt the event
     */
    public void onWorldLoad(WorldEvent.Load evt) {}
    
    /**
     * Executes when the HUD-informations should be displayed
     * 
     * @param main the main project color
     * @param sub the secondary project color
     * 
     * @return optionally the information that should be displayed
     */
    public String[] onInformationEvent(char main,char sec) {
    	return new String[0];
    }

    /**
     * Enables the module
     * @return if any conflicting modules are still active
     */
    public final Optional<Module[]> enable() {
        //Checks if the module is already enabled
        if(this.active)
            return Optional.empty();

        //Sets the enabled state to true
        this.active=true;
        //Fires the enable event
        this.onEnable();
        //Fires the toggle event for the event manager
        Toolsmod.getInstance().getModuleManager().handleModUpdate(this);
        return Optional.empty();
    }

    /**
     * Disables the module
     */
    public final void disable() {
        //Checks if the module is already disabled
        if(!this.active)
            return;
        //Sets the enabled state to false
        this.active=false;
        //Executes the disable event
        this.onDisable();
        //Fires the toggle event on the module manager
        Toolsmod.getInstance().getModuleManager().handleModUpdate(this);
    }

    /**
     * Toggles the module
     * @returns if the toggle was successful or if any module were conflicting
     */
    public final boolean toggle() {
        if(this.active) {
            this.disable();
            return true;
        }
    	//Gets the conflicting modules
    	Optional<Module[]> optConf = this.enable();
    	
    	//Checks if any are conflicting
    	if(optConf.isPresent()) {
    		Module[] conflicting = optConf.get();
    		//Formats them
    		String formatted = Arrays.stream(conflicting).map(i->i.getName()).collect(Collectors.joining(", "));
    	
    		//Sends the error
    		TextUtil.getInstance().sendError("global.module.toggle.error.incompatible."+(conflicting.length>1?"multi":"one"), formatted,this.getName());
    		return false;
    	}
    	return true;
    }

    public String getName() {
        return this.name;
    }
    public String getDescription() {
        return TextUtil.getInstance().getByKey("module."+this.getName().toLowerCase()+".description");
    }
    public ModuleCategory getCategory() {
        return this.category;
    }
    public int getKeyBind() {
        return this.keyBind;
    }
    public void setKeyBind(int keyBind) {
        this.keyBind = keyBind;
    }
    public boolean isActive() {
        return this.active;
    }
    public boolean isAvaiableAtStart() {
		return this.avaiableAtStart;
	}
    public GuiGroup<GuiQuickAccess> getQuickAccessGui() {
		return this.quickAccessGui;
	}
    public boolean isAllowed() {
		return this.allowed;
	}
    public void setAllowed(boolean allowed) {
		this.allowed = allowed;
	}
    
    public Set<Setting<?>> getSettings() {
		return this.settings;
	}
}

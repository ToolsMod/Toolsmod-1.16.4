package de.whiletrue.toolsmod.module.defined.tools;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import de.whiletrue.toolsmod.gui.GuiGroup;
import de.whiletrue.toolsmod.gui.quickaccess.GuiQuickAccess;
import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.module.ModuleCategory;
import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.module.defined.tools.headwriter.Alphabet;
import de.whiletrue.toolsmod.module.defined.tools.headwriter.gui.GuiQaHeadWriterText;
import de.whiletrue.toolsmod.module.defined.tools.headwriter.gui.GuiQaHeadWriterType;
import de.whiletrue.toolsmod.util.classes.FileUtil;
import de.whiletrue.toolsmod.util.classes.ItemUtil;
import de.whiletrue.toolsmod.util.classes.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;

public class ModuleHeadWriter extends Module {

	// Resourcelocation with all heads
	private static final ResourceLocation LOCATION_HEADS = new ResourceLocation(Toolsmod.ID,
			"module/headwriter/heads.json");

	//Gui to start the module
	private static final GuiGroup<GuiQuickAccess> ENABLE_GUI = GuiGroup.of(
		new GuiQaHeadWriterText(),
		new GuiQaHeadWriterType()
	);
	
	// All alphabets that got loaded
	public List<Alphabet> loadedAlphabets = new ArrayList<>();
	// Currently selected alphabet
	public Alphabet selected;

	// List with all heads that are remaining
	private List<ItemStack> remaningHeads = new ArrayList<>();

	// Usedtext
	private String usedAlphabet, text;

	// If the writing is enabled
	private boolean enabled;

	public ModuleHeadWriter() {
		super("HeadWriter", ModuleCategory.TOOLS, false);

		// Gets the content
		this.loadedAlphabets = FileUtil.getInstance().loadFromRSCAsJson(LOCATION_HEADS).get()
				// Parses the content to json
				.getAsJsonObject().entrySet().stream()
				// Maps them to their alphabets
				.map(i -> new Alphabet(i.getKey(), i.getValue().getAsJsonObject()))
				// Collects them
				.collect(Collectors.toList());
		//Selects the first alphabet
		this.selected=this.loadedAlphabets.get(0);
	}

	@Override
	public void onEnable() {
		// Checks if the module got enabled from the GUI
		if (!this.enabled) {
			this.disable();
			//Opens the open gui
			ENABLE_GUI.open();
			return;
		}

		// Reset
		this.enabled = false;
		// Start the the first head
		this.giveNextHead();
	}

	@Override
	public void onPlace(EntityPlaceEvent event) {
		// Checks the item
		if (!this.mc.player.getHeldItemMainhand().getItem().equals(Items.PLAYER_HEAD))
			return;

		// Checks if player is creative
		if (!Minecraft.getInstance().player.abilities.isCreativeMode) {
			TextUtil.getInstance().sendError("modules.headwriter.error.gm");
			this.disable();
			return;
		}

		// Gets the next item
		this.giveNextHead();
	}
	
	@Override
    public void onDisable() {
        //Reset
        this.enabled=false;
    }
    
    @Override
    public String[] onInformationEvent(char main,char sec) {
    	return new String[] {
			String.format("§%cText: §%c%s",main,sec,this.text),
			String.format("§%cAlphabet: §%c%s",main,sec,this.usedAlphabet),
			String.format("§%cRemaining: §%c%d",main,sec,this.remaningHeads.size()+1),	
    	};
    }

    /**
     * Gives the next head to the player
     */
    private void giveNextHead() {
        //Checks if all got placed
        if(this.remaningHeads.isEmpty()) {
            this.disable();
            ItemUtil.getInstance().setItem(ItemStack.EMPTY);
            return;
        }

        //Sends the next head
        ItemUtil.getInstance().setItem(this.remaningHeads.get(0));
        this.remaningHeads.remove(0);
        
		//Updates the info display
		Toolsmod.getInstance().getModuleManager().updateInfoDisplay();
    }

    /**
     * Starts the head-writer
     * @param text
     */
    public void startWriting(String text){
    	//Disables the module
    	this.disable();
    	
        //Checks if the alphabet's are loaded
        if(this.loadedAlphabets==null || this.loadedAlphabets.isEmpty()){
            TextUtil.getInstance().sendError("modules.headwriter.error.unload");
            return;
        }
        
        //Checks if player is creative
        if(!Minecraft.getInstance().player.abilities.isCreativeMode){
            TextUtil.getInstance().sendError("modules.headwriter.error.gm");
            return;
        }
        
        //Clears the list
        this.remaningHeads.clear();

        //For every characters
        for(char c:text.toCharArray()){
            //Gets the id
            String id = this.selected.getIDFromChar(c);

            //Creates the b64 encoded component
            CompoundNBT b64 = new CompoundNBT(){{
                CompoundNBT textures = new CompoundNBT(){{
                    CompoundNBT skin = new CompoundNBT(){{
                        this.putString("url","http://textures.minecraft.net/texture/"+id);
                    }};
                    this.put("SKIN",skin);
                }};
                this.put("textures",textures);
            }};

            //Encodes the b64 head
            String headB64 = Base64.getEncoder().encodeToString(b64.toString().getBytes());

            //Creates the nbt compound
            CompoundNBT nbt = new CompoundNBT(){{
               CompoundNBT skullOwner = new CompoundNBT(){{
            	   this.putUniqueId("Id", UUID.randomUUID());

                   CompoundNBT properties = new CompoundNBT(){{
                       ListNBT textures = new ListNBT(){{
                            CompoundNBT val = new CompoundNBT(){{
                                this.putString("Value",headB64);
                            }};
                            this.add(val);
                       }};
                       this.put("textures",textures);
                   }};
                   this.put("Properties",properties);
               }};
               this.put("SkullOwner",skullOwner);
            }};

            //Char for colors
            char p = 167;

            //Creates the stack
            ItemStack head = new ItemStack(Items.PLAYER_HEAD);
            head.setTag(nbt);
            head.setDisplayName(new StringTextComponent(String.format(
                    "%c8[%c%c"+this.getName()+"%c8]: %c%c%s",
                    p,p,
                    Toolsmod.COLOR_MAIN,
                    p,p,
                    Toolsmod.COLOR_SECONDARY,
                    text
                    )));

            //Adds the stack
            this.remaningHeads.add(head);
        }

        //Updates the info's
        this.text=text;
        this.usedAlphabet=this.selected.getName();

        //Closes the GUI
        this.mc.displayGuiScreen(null);
        
        //Starts the module
        this.enabled=true;
        this.enable();
    }

}

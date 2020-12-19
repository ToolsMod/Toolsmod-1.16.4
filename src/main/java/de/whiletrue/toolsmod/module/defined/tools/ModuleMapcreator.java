package de.whiletrue.toolsmod.module.defined.tools;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import de.whiletrue.toolsmod.gui.GuiGroup;
import de.whiletrue.toolsmod.gui.quickaccess.GuiQuickAccess;
import de.whiletrue.toolsmod.module.ModuleCategory;
import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.module.defined.tools.mapcreator.BlockSelection;
import de.whiletrue.toolsmod.module.defined.tools.mapcreator.EnumRenderSettings;
import de.whiletrue.toolsmod.module.defined.tools.mapcreator.gui.GuiQaMapcreatorColor;
import de.whiletrue.toolsmod.module.defined.tools.mapcreator.gui.GuiQaMapcreatorCreate;
import de.whiletrue.toolsmod.module.defined.tools.mapcreator.gui.GuiQaMapcreatorImage;
import de.whiletrue.toolsmod.module.defined.tools.mapcreator.gui.GuiQaMapcreatorPosition;
import de.whiletrue.toolsmod.module.defined.tools.mapcreator.gui.GuiQaMapcreatorRender;
import de.whiletrue.toolsmod.settings.defined.SettingKeybind;
import de.whiletrue.toolsmod.util.ColorConverter;
import de.whiletrue.toolsmod.util.Keybind;
import de.whiletrue.toolsmod.util.Timer;
import de.whiletrue.toolsmod.util.classes.ImageUtil;
import de.whiletrue.toolsmod.util.classes.JavaUtil;
import de.whiletrue.toolsmod.util.classes.RenderUtil;
import de.whiletrue.toolsmod.util.classes.TextUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.network.play.client.CChatMessagePacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.event.TickEvent.ClientTickEvent;

public class ModuleMapcreator extends Module{

	//On which key the gui opens
	public SettingKeybind sGuiBind = new SettingKeybind()
			.name("guiKeybind")
			.standard(new Keybind(GLFW.GLFW_KEY_F,KeyModifier.NONE));
	
	//Full and scaled images
	private BufferedImage loaded,scaled;
	
	//Current scale
	private int scale = 4;
	//Set position
	private BlockPos pos;
	
	//Settings for the rendering
	private EnumRenderSettings rendering = EnumRenderSettings.CORNERS;
	
	//Color converter
	private ColorConverter converter;
	
	//Loaded blocks
	private Map<Color, BlockSelection> blockPalete = new HashMap<>();
	
	//List with all setblock commands that still need to execute
	private List<String> loadCommands = new ArrayList<>();
	//Timer for the delay
	private Timer timer = new Timer();
	//Delay for the setting
	private int delay;
	
	//GUI for the settings
	private GuiGroup<GuiQuickAccess> guis = GuiGroup.of(
		new GuiQaMapcreatorImage(),
		new GuiQaMapcreatorRender(),
		new GuiQaMapcreatorPosition(),
		new GuiQaMapcreatorColor(),
		new GuiQaMapcreatorCreate()
	);
	
	//Preset invalid blocks (Should not be registered)
	private static final Block[] INVALID_BLOCK = {
		Blocks.AIR,
		Blocks.PISTON_HEAD
	};
	
	@SuppressWarnings("deprecation")
	public ModuleMapcreator() {
		super("Mapcreator", ModuleCategory.TOOLS, false);
		
		//Resets the block palete
		this.blockPalete=new HashMap<>();
		
		//Fills the block palete
		Registry.BLOCK.forEach(i->{
			
			//Checks if the block is preset invalid
			if(Arrays.stream(INVALID_BLOCK).filter(x->i==x).count()>0)
				return;
			
			//TODO old:
			/*
			//Checks if the block is invalid
			if(!i.isSolid(i.getDefaultState()))
				return;
			 * 
			 * */
			
			//Gets the color from the block
			MaterialColor c = i.getMaterialColor();

			//Checks if the color should not be added
			if(c.colorValue == 0x007c00 || c.colorValue == 0)
				return;

			//Gets the actual color
			Color asC = new Color(c.colorValue);
			
			//Checks if the color exists
			if(this.blockPalete.containsKey(asC))
				//Appends the block
				this.blockPalete.get(asC).getAvailableBlocks().add(i);
			else
				//Creates a new selection section
				this.blockPalete.put(asC, new BlockSelection(asC,i));
		});
		
		//Creates the converter
		this.converter=new ColorConverter(this.blockPalete.keySet().toArray(new Color[this.blockPalete.keySet().size()]));
	}
	
	@Override
	public void onEnable() {
		//Clears any commands
		this.loadCommands.clear();
	}
	
	@Override
	public void onRender3D(RenderWorldLastEvent event) {
		//Checks if the image is loaded, the position is set the player is not to far away
		if(this.scaled==null || this.pos==null || !this.pos.withinDistance(this.mc.player.getPositionVec(), 200))
			return;

		GL11.glPushMatrix();
		{
			switch (this.rendering) {
			case ALL:
				this.renderAll();
				break;
			case CORNERS:
				this.renderCorners();
				break;
			case NEAR_ME:
				this.renderNearMe();
				break;
			default:
				break;
			}
		}
		GL11.glPopMatrix();
	}
	
	@Override
	public void onKeyToggled(KeyInputEvent event) {
		//Opens the gui
		if(this.sGuiBind.value.isKeycodeMatching(event.getKey()))
			this.guis.open();
	}
	
	@Override
	public void onTick(ClientTickEvent event) {
		//Checks if a commands is present and the timer is ready
		if(this.loadCommands.isEmpty() || !this.timer.hasReached(this.delay))
			return;
		
		do{
			//Reset timer
			this.timer.reset();
			
			//Gets the next command
			String cmd = "/setblock "+this.loadCommands.get(0);
			this.loadCommands.remove(0);
			
			//Executes the command
			this.mc.player.connection.sendPacket(new CChatMessagePacket(cmd));
			
			//Checks if the image is finished
			if(this.loadCommands.isEmpty())
				TextUtil.getInstance().sendMessage("modules.mapcreator.finish");
			
		}while(this.delay==0 && !this.loadCommands.isEmpty());
	}
	
	/**
	 * Creates the image and loads the required commands
	 */
	public String create(float delay) {
		//Sets the delay
		this.delay=(int) (delay*1000);
		//Clears any remaining commands
		this.loadCommands.clear();
		
		//Checks if the image is loaded and position is set
		if(this.scaled==null || this.pos==null)
			return TextUtil.getInstance().getByKey("modules.mapcreator.start.failed");
		
		//Closes the gui
		this.mc.displayGuiScreen(null);
		
		//For the whole image
		for(int x=0;x<this.scaled.getWidth();x++)
			for(int y=0;y<this.scaled.getHeight();y++) {
				//Checks if the block is transparent
				if(this.scaled.getRGB(x, y)>>24 == 0)
					continue;
				
				//Get color
				Color color = new Color(this.scaled.getRGB(x, y));
				
				//Gets the nearest color
				Color nearest = this.converter.nearestColor(color);
				
				//Gets the block
				Block b = this.blockPalete.get(nearest).getSelected();

				//The new position
				BlockPos set = this.pos.add(x,0,y);
				
				//Skips positions that already having the right blocks
				if(mc.world.getBlockState(set).getBlock().equals(b))
					continue;
				
				//Adds the command
				this.loadCommands.add(String.format("%d %d %d minecraft:%s",
						set.getX(),
						set.getY(),
						set.getZ(),
						b.getTranslationKey().substring("block.minecraft.".length())));
			}
		
		//Returns the information
		return TextUtil.getInstance().getByKey("modules.mapcreator.start");
	}
	
	/**
	 * Renders all of the whole image
	 */
	private void renderAll() {
		//Iterates over the whole image
		for(int x=0;x<this.scaled.getWidth();x++)
			for(int y=0;y<this.scaled.getHeight();y++) {
				//Get color
				Color color = new Color(this.scaled.getRGB(x, y));
				
				//Renders the pixel
				GL11.glColor3f(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f);
				RenderUtil.getInstance().renderBlockOverlay(this.pos.getX()+x, this.pos.getY(), this.pos.getZ()+y, .7f);
			}
	}
	
	/**
	 * Renders all pixels that are near the player
	 */
	private void renderNearMe() {
		//Player position
		BlockPos pos = this.mc.player.getPosition();
		
		//Iterates over the whole image
		for(int x=0;x<this.scaled.getWidth();x++)
			for(int y=0;y<this.scaled.getHeight();y++) {
				//New block pos
				BlockPos renderPos = this.pos.add(x,0,y);
				
				//Checks if the distance is low enough
				if(renderPos.distanceSq(pos) > 30)
					continue;
				
				//Get color
				Color color = new Color(this.scaled.getRGB(x, y));
				
				//Renders the pixel
				GL11.glColor3f(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f);
				RenderUtil.getInstance().renderBlockOverlay(renderPos.getX(), renderPos.getY(),renderPos.getZ(), .7f);
			}
	}
	
	/**
	 * Renders the corners
	 */
	private void renderCorners() {
		BlockPos[] positions = {
			this.pos,
			this.pos.add(0,0,this.scale*16-1),
			this.pos.add(this.scale*16-1,0,0),
			this.pos.add(this.scale*16-1,0,this.scale*16-1)
		};
		
		//Over all corners
		for(BlockPos pos : positions) {			
			//Renders the pixel
			GL11.glColor3f(0xFF, 0xFF, 0xFF);
			RenderUtil.getInstance().renderBlockOverlay(pos.getX(), pos.getY(), pos.getZ(), 2);
		}
	}
	
	/**
	 * Sets the new image
	 * @param img the new image
	 */
	public void setImage(BufferedImage img) {
		this.loaded=img;
		this.scaleImage(this.scale);
	}
	
	/**
	 * Updates the scaled image
	 */
	public void scaleImage(int scale) {
		this.scale=scale;
		
		//Resizes the image
		if(this.loaded!=null)
			this.scaled=ImageUtil.getInstance().resize(this.loaded, 16*this.scale, 16*this.scale);
	}
	
	public EnumRenderSettings switchRendering(boolean direction) {
		return this.rendering=direction?JavaUtil.getInstance().getEnumNext(this.rendering):JavaUtil.getInstance().getEnumPre(this.rendering);
	}
	
	public Map<Color, BlockSelection> getColorPalete() {
		return this.blockPalete;
	}
	
	public void setPosition(BlockPos pos) {
		this.pos = pos;
	}
	
	public int getScale() {
		return this.scale;
	}
	
	public EnumRenderSettings getRendering() {
		return this.rendering;
	}
	
	public GuiGroup<GuiQuickAccess> getIngameGui() {
		return this.guis;
	}
	
	public List<String> getLoadCommands() {
		return this.loadCommands;
	}

}

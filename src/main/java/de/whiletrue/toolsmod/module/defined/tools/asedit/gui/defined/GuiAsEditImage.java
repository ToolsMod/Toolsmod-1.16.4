package de.whiletrue.toolsmod.module.defined.tools.asedit.gui.defined;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import de.whiletrue.toolsmod.gui.widgets.TmAnimatedText;
import de.whiletrue.toolsmod.gui.widgets.TmBackgroundWidget;
import de.whiletrue.toolsmod.gui.widgets.TmTextWidget;
import de.whiletrue.toolsmod.gui.widgets.TmTextfield;
import de.whiletrue.toolsmod.gui.widgets.TmUpdateButton;
import de.whiletrue.toolsmod.gui.widgets.rounding.GuiNode;
import de.whiletrue.toolsmod.module.defined.tools.asedit.EditableArmorStand;
import de.whiletrue.toolsmod.module.defined.tools.asedit.gui.GuiAsEdit;
import de.whiletrue.toolsmod.util.ColorConverter;
import de.whiletrue.toolsmod.util.TextAlign;
import de.whiletrue.toolsmod.util.classes.ImageUtil;
import de.whiletrue.toolsmod.util.classes.JavaUtil;
import de.whiletrue.toolsmod.util.classes.TextUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;

public class GuiAsEditImage extends GuiAsEdit{

	//Max and min image size
	private static final int MAX_SIZE = 100;
	private static final int MIN_SIZE = 1;
	
	//Image renderer
	private TmTextWidget imageRenderer;

	//Loaded text
	private String text;
	
	//Currently loaded image
	private BufferedImage loadedImage;
	//Image size
	private int sizeX=80,sizeY=80;
	
	//Color model and indexes
	private char[] colorChars = {'4', 'c', '6', 'e', '2', 'a', 'b', '3', '1', '9', 'd', '5', 'f', '7', '8', '0'};
	private ColorConverter converter = new ColorConverter(new int[] {0xAA0000, 0xFF5555, 0xFFAA00, 0xFFFF55, 0x00AA00, 0x55FF55, 0x55FFFF, 0x00AAAA, 0x0000AA, 0x5555FF, 0xFF55FF, 0xAA00AA, 0xFFFFFF, 0xAAAAAA, 0x555555, 0x0});
	
	//Information display
	private TmAnimatedText info;
	
	public GuiAsEditImage() {
		super("image.name");
	}

	@Override
	protected void init() {
		super.init();
		
		//Gets the nodes
		GuiNode imageNode = this.getImageFieldNode();
		GuiNode barNode = this.getLoadNode();
		GuiNode settingsNode = this.getSettingsNode();
		
		//Calculates the space between the nodes
		int biggerY = Math.max(imageNode.getHeight(), settingsNode.getHeight());
		int spaceX = (this.overlayWidth-imageNode.getWidth()-settingsNode.getWidth())/3;
		int spaceY = (this.height-biggerY-barNode.getHeight())/3;
		
		
		//Init's the nodes
		barNode.init(this.overlayX+this.overlayWidth/2-barNode.getWidth()/2, spaceY);
		imageNode.init(this.overlayX+spaceX*1, spaceY*2+barNode.getHeight()+biggerY/2-imageNode.getHeight()/2);
		settingsNode.init(this.overlayX+spaceX*2+imageNode.getWidth(), spaceY*2+barNode.getHeight()+biggerY/2-settingsNode.getHeight()/2);

		//Adds the text info holder
		this.addWidget(this.info = new TmAnimatedText(this.overlayX+this.overlayWidth/2, (int) (this.height*.05f)).setTransition(500, 5000, 500));
		this.info.setScale(2).setSpecialRender(true);
	}
	
	/**
	 * Node for the image field
	 */
	private GuiNode getImageFieldNode() {
		//Gets the size of the node
		final int size = (int) (Math.min(this.overlayWidth, this.height)*.8f);
		
		return new GuiNode(size,size,(x,y)->{
			//Adds the background
			this.addWidget(new TmBackgroundWidget(x, y, size, size, 0xa0000000).setOutline(0xff000000,1));
			
			//Renders the image
			this.addWidget(this.imageRenderer = new TmTextWidget(x+size/2, y+size/2, "", 0xffffffff)
					.setAlignX(TextAlign.MIDDLE)
					.setAlignY(TextAlign.MIDDLE));
			this.imageRenderer.setScale(0.35f);
		});
	}
	
	/**
	 * Node for the image loading
	 */
	private GuiNode getLoadNode() {
		//Gets the size of the node
		final int w = (int) (this.overlayWidth*.8f);
		final int h = 30;
		
		return new GuiNode(w,h,(x,y)->{
			//Adds the background
			this.addWidget(new TmBackgroundWidget(x, y, w, h, 0xa0000000).setOutline(0xff000000,1));
						
			//Adds the input field
			TmTextfield tf = new TmTextfield(x+5, y+h/2-7, w-90, 15, "",null);
			tf.setMaxStringLength(Integer.MAX_VALUE);
			this.addWidget(tf);
			
			//Adds the load button
			this.addWidget(new TmUpdateButton(x+w-80, y+h/2-10, 70, 20, btn->{
				if(btn!=null)
					this.loadImage(tf.getText());
				return TextUtil.getInstance().getByKey("modules.asedit.gui.image.button.preview");
			}));
		});
	}
	
	/**
	 * Node for the settings
	 */
	private GuiNode getSettingsNode() {
		//Gets the size of the node
		final int size = (int) (Math.min(this.overlayWidth, this.height)*.8f);
		
		//Gets the size of the node
		final int w = (int) Math.min(((this.overlayWidth-size)*.8f),120);
		final int h = (int) (this.height*.5f);
		
		//Size of the img-size fields
		final int imgSizeW = (int) (w*.6f);
		
		//Validator
		Predicate<String> validator = str->{
			return str.isEmpty() || JavaUtil.getInstance().isInt(str);
		};
		
		//Handler
		BiConsumer<Integer,Boolean> handler = (val,isX)->{
			//Updates the value
			if(isX)
				this.sizeX=val;
			else
				this.sizeY=val;

			//Loads the text
			this.generateText();
		};
		
		return new GuiNode(w,h,(x,y)->{
			//Adds the background
			this.addWidget(new TmBackgroundWidget(x, y, w, h, 0xa0000000).setOutline(0xff000000,1));
			
			//Adds the size settings
			this.addWidget(new TmTextWidget(x+w/2-imgSizeW/2, y+31, "X:", 0xffffffff));
			TmTextfield fX = new TmTextfield(x+w/2-(imgSizeW-10)/2+5, y+30, imgSizeW-10, 10,
					String.valueOf(this.sizeX),
					i->handler.accept(i.isEmpty()?0:Integer.valueOf(i), true));
			fX.setValidator(validator);
			this.addWidget(fX);

			this.addWidget(new TmTextWidget(x+w/2-imgSizeW/2, y+61, "Y:", 0xffffffff));
			TmTextfield fY = new TmTextfield(x+w/2-(imgSizeW-10)/2+5, y+60, imgSizeW-10, 10,
					String.valueOf(this.sizeY),
					i->handler.accept(i.isEmpty()?0:Integer.valueOf(i), false));
			fY.setValidator(validator);
			this.addWidget(fY);
			
			//Adds the load button
			this.addWidget(new TmUpdateButton(x+w/2-40, y+h-40, 80, 20, btn->{
				if(btn!=null) {
					//Checks if the image is event loaded
					if(this.text==null)
						this.info.showKey(0xffFF3D3D,"modules.asedit.gui.image.error.image");
					//Checks if the image is set to be to big
					else if(this.sizeX>MAX_SIZE || this.sizeX<MIN_SIZE ||this.sizeY>MAX_SIZE || this.sizeY<MIN_SIZE)
						this.info.showKey(0xffFF3D3D,"modules.asedit.gui.image.error.resize",MIN_SIZE,MAX_SIZE);
					else {
						//Splits all lines
						String[] split = this.text.split("\n");
						
						//Gets the default player position
						Vector3d pos = this.minecraft.player.getPositionVec();
						
						//Unselect's all other stand
						this.manager.getStands().forEach(i->i.setSelected(false));
						
						//Iterates over all lines
						for(int i=split.length-1;i>=0;i--) {
							//Gets the name
							String name = split[split.length-i-1];
							
							//Checks if the name is set
							if(name.trim().isEmpty())
								continue;
							
							//Creates the new stand
							EditableArmorStand stand = new EditableArmorStand(pos.getX(), pos.getY()+i*.2, pos.getZ());
							//Sets the properties
							stand.setInvisible(true);
							stand.setInvulnerable(true);
							stand.setNoGravity(true);
							stand.setDisabledSlots(true);
							stand.setReferenceName("Image-stand");
							stand.setCustomName(new StringTextComponent(name));
							stand.setCustomNameVisible(true);
							//Adds the stand
							this.manager.addAll(stand);
							stand.setSelected(true);
						}
						
						//Selects them all
						this.manager.handleSelect(-1);
						
						//Closes the GUI
						this.onClose();
					}
					
				}
				return TextUtil.getInstance().getByKey("modules.asedit.gui.image.button.load");
			}));
		});
	}
	
	/**
	 * Loads the image from the given file
	 * @param file the file
	 */
	public void loadImage(String file) {
		//Tries to load the image
		Optional<BufferedImage> optImg = ImageUtil.getInstance().loadImage(file);
		
		//Checks if the img got loaded
		if(!optImg.isPresent()) {
			//Displays the error
			this.info.showKey(0xFF3D3D,"modules.asedit.gui.image.error.load");
			return;
		}
		
		//Sets the loaded image
		this.loadedImage = optImg.get();
		
		//Loads the text
		this.generateText();
	}

	/**
	 * Generates the text from the image
	 */
	public void generateText() {
		//Checks if the image is loaded
		if(this.loadedImage==null)
			return;
		
		//Buffer for the text
		StringBuffer sb = new StringBuffer();
		
		//Gets the resized version of the image
		BufferedImage resized = ImageUtil.getInstance().resize(
			this.loadedImage,
			MathHelper.clamp(this.sizeX, MIN_SIZE, MAX_SIZE),
			MathHelper.clamp(this.sizeY, MIN_SIZE, MAX_SIZE)
		);
		
		//Iterates over each pixel
		for(int y=0;y<resized.getHeight();y++) {
			for(int x=0;x<resized.getWidth();x++) {
				//Checks if the pixel is transparent
				if(resized.getRGB(x, y) >> 24 == 0)
					continue;
				//Gets the nearest color
				char color = this.colorChars[this.converter.nearestIndex(new Color(resized.getRGB(x, y)))];
				
				//Appends the color
				sb.append("§"+color+(char)0x25a0);
			}
			//Appends a new line
			sb.append("\n");
		}
		
		//Gets the size of the node
		final int size = (int) (Math.min(this.overlayWidth, this.height)*.8f);

		//Sets the text
		this.text=sb.toString();
		this.imageRenderer.setText(this.text.split("\n"));
		this.imageRenderer.setScale((1-((float)Math.max(this.sizeX, this.sizeY)/(float)size))*.5f);
	}
}

package de.whiletrue.toolsmod.module.defined.tools.mapcreator.gui;

import java.awt.image.BufferedImage;
import java.util.Optional;

import de.whiletrue.toolsmod.gui.quickaccess.GuiQuickAccess;
import de.whiletrue.toolsmod.gui.widgets.TmSlider;
import de.whiletrue.toolsmod.gui.widgets.TmTextWidget;
import de.whiletrue.toolsmod.gui.widgets.TmTextfield;
import de.whiletrue.toolsmod.gui.widgets.TmUpdateButton;
import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.module.defined.tools.ModuleMapcreator;
import de.whiletrue.toolsmod.util.classes.ImageUtil;
import de.whiletrue.toolsmod.util.classes.TextUtil;

public class GuiQaMapcreatorImage extends GuiQuickAccess{

	//Reference to the module
	private ModuleMapcreator module;

	// Image input field
	public TmTextfield imageField = new TmTextfield(0, 0, 0, 0, "", null).setMaxStringLength(Integer.MAX_VALUE);

	// Image submit button
	private TmUpdateButton imageSubmit = (TmUpdateButton) new TmUpdateButton(0, 0, 0, 0, this::handleSubmitImage);

	// Image size slider
	private TmSlider sizeSlider = new TmSlider(0, 0, 0, 0, 4, 16, 4, this::updateImageSize);

	// Loading thread
	private Thread loadThread;

	public GuiQaMapcreatorImage() {
		super("modules.mapcreator.gui.image.name", "modules.mapcreator.gui.image.name.short", 360, 210);
	}

	@Override
	protected void init() {
		// Loads the module
		this.module = Toolsmod.getInstance().getModuleManager().getModuleByClass(ModuleMapcreator.class).get();

		// Adds all widgets
		this.addWidgetWithListener(this.imageField, (x,y)->new float[] {
			this.width/2-x/2+60,
			this.height/2+y*.1f,
			x-70,
			15
		});
		
		this.addWidgetWithListener(this.imageSubmit
				.setTooltippByKey("modules.mapcreator.gui.image.tooltipp"),(x,y)->new float[] {
			this.width/2-40+25,
			this.height/2+y*.28f,
			80,
			20
		});
		this.addWidgetWithListener(this.sizeSlider,(x,y)->new float[] {
			this.width/2-x/2+100,
			this.height/2-y*.2f,
			x-150,
			15
		});
		this.sizeSlider.setByValue(this.module.getScale());

		this.addWidgetWithListener(new TmTextWidget(0, 0, "", 0xffffffff)
				.setTextByKey("modules.mapcreator.gui.image.text.img"), (x,y)->new float[] {
			this.width/2+25,
			this.height/2+y*.1f-14
		});
		this.addWidgetWithListener(new TmTextWidget(0, 0, "", 0xffffffff)
				.setTextByKey("modules.mapcreator.gui.image.text.size"), (x,y)->new float[] {
			this.width/2+25,
			this.height/2-y*.25f
		});
	
	}

	/**
	 * Executes when the button gets clicked
	 * @param btn the button
	 */
	private String handleSubmitImage(TmUpdateButton btn) {
		
		//Kills any pending threads
		if(this.loadThread!=null && !this.loadThread.isInterrupted())
			this.loadThread.interrupt();
		
		//Starts loading the image
		this.loadThread=new Thread(()->{			
			submit:if(btn!=null) {
				if(this.imageField.getText().trim().isEmpty())
					break submit;
				
				//Tries to load the image
				Optional<BufferedImage> optImg = ImageUtil.getInstance().loadImage(this.imageField.getText().trim());

				if(optImg.isPresent()) {
					this.module.setImage(optImg.get());
					this.imageField.setText("");
					TextUtil.getInstance().sendMessage("modules.mapcreator.gui.image.load.success");
				}else
					TextUtil.getInstance().sendError("modules.mapcreator.gui.image.load.failed");
			}
		});
		this.loadThread.start();
		
		return TextUtil.getInstance().getByKey("modules.mapcreator.gui.image.load.button");
	}
	
	/**
	 * Exeuctes when the image-size slider updates
	 * @param perc
	 * @param value
	 * @return
	 */
	private String updateImageSize(TmSlider slider,float value) {
		if(this.module!=null)	
			//Resizes the image
			this.module.scaleImage((int)value);
		
		return TextUtil.getInstance().getByKey("modules.mapcreator.gui.image.size", (int)value,16);
	}
	
}

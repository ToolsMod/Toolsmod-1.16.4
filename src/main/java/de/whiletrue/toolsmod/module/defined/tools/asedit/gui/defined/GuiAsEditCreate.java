package de.whiletrue.toolsmod.module.defined.tools.asedit.gui.defined;

import de.whiletrue.toolsmod.gui.widgets.TmAnimatedText;
import de.whiletrue.toolsmod.gui.widgets.TmBackgroundWidget;
import de.whiletrue.toolsmod.gui.widgets.TmTextWidget;
import de.whiletrue.toolsmod.gui.widgets.TmUpdateButton;
import de.whiletrue.toolsmod.gui.widgets.rounding.GuiNode;
import de.whiletrue.toolsmod.module.defined.tools.asedit.gui.GuiAsEdit;
import de.whiletrue.toolsmod.util.classes.TextUtil;

public class GuiAsEditCreate extends GuiAsEdit{

	//Info display
	private TmAnimatedText info;
	
	public GuiAsEditCreate() {
		super("create.name");
	}
	
	@Override
	protected void init() {
		super.init();
		
		//Gets all nodes
		GuiNode infoNode = this.getInfoNode();
		GuiNode createNode = this.getCreateNode();
		
		//Calculates the space between them
		int spaceY = (this.height-infoNode.getHeight()-createNode.getHeight())/3;

		//Init's the node
		infoNode.init(this.overlayX+this.overlayWidth/2-infoNode.getWidth()/2, spaceY);
		createNode.init(this.overlayX+this.overlayWidth/2-createNode.getWidth()/2, spaceY*2+infoNode.getHeight());
		
		//Adds the text info holder
		this.addWidget(this.info = new TmAnimatedText(this.overlayX+this.overlayWidth/2, (int) (this.height*.05f)).setTransition(500, 5000, 500));
		this.info.setScale(2);
	}

	/**
	 * Node for the create button
	 */
	private GuiNode getCreateNode() {
		//Size of the node
		final int w = 120;
		final int h = 40;
		
		return new GuiNode(w,h,(x,y)->{
			//Adds the background
			this.addWidget(new TmBackgroundWidget(x, y, w, h, 0xa0000000).setOutline(0xff000000,1));
			
			//Adds the create button
			this.addWidget(new TmUpdateButton(x+w/2-40, y+h/2-10, 80, 20, btn->{
				if(btn != null)
					if(this.module.startPlaceing())
	                	this.minecraft.displayGuiScreen(null);
	                else
	                	//Adds the info
	                	this.info.showKey(0xFF6A6A,"modules.asedit.gui.create.create.error.gm");
				return TextUtil.getInstance().getByKey("modules.asedit.gui.create.create.button");
			}));
		});
	}
	
	/**
	 * Node for the info box
	 */
	private GuiNode getInfoNode() {
		
		//Min and max width
		final int minWidth = (int) Math.min(this.overlayWidth*.65f,250);
		final int maxWidth = (int) Math.min(this.overlayWidth*.8f,300);
		
		//Gets the text
		String infoText = TextUtil.getInstance().getByKey("modules.asedit.gui.create.info");
		
		//Splits the info into lined segments
		String[] splitInfo = TextUtil.getInstance().splitStringOnWidth(infoText, minWidth, maxWidth-20).split("\n");

		return new GuiNode(maxWidth, splitInfo.length*10+5, (x,y)->{			
			//Adds the info text background
			this.addWidget(new TmBackgroundWidget(x, y, maxWidth, splitInfo.length*10+5, 0xa0000000).setOutline(0xff000000,1));
			
			//Adds the info text
			this.addWidget(new TmTextWidget(x+maxWidth/2, y+5,null,0xffffffff)
					.setText(splitInfo));
		});
	}
	
}

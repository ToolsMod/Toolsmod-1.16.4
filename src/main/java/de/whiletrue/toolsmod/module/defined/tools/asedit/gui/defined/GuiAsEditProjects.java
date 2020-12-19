package de.whiletrue.toolsmod.module.defined.tools.asedit.gui.defined;

import java.io.File;
import java.util.Optional;

import de.whiletrue.toolsmod.gui.widgets.TmAnimatedText;
import de.whiletrue.toolsmod.gui.widgets.TmBackgroundWidget;
import de.whiletrue.toolsmod.gui.widgets.TmTextWidget;
import de.whiletrue.toolsmod.gui.widgets.TmTextfield;
import de.whiletrue.toolsmod.gui.widgets.TmUpdateButton;
import de.whiletrue.toolsmod.gui.widgets.rounding.GuiNode;
import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.module.defined.tools.asedit.gui.GuiAsEdit;
import de.whiletrue.toolsmod.module.defined.tools.asedit.gui.defined.projects.GuiAsEditProjectsLoad;
import de.whiletrue.toolsmod.module.defined.tools.asedit.projects.AsEditProject;
import de.whiletrue.toolsmod.module.defined.tools.asedit.projects.AsEditProjectManager;
import de.whiletrue.toolsmod.module.defined.tools.asedit.projects.EnumProjectVersion;
import de.whiletrue.toolsmod.util.TextAlign;
import de.whiletrue.toolsmod.util.classes.TextUtil;

public class GuiAsEditProjects extends GuiAsEdit{

	//Information display
	private TmAnimatedText info;
	
	//Reference to the project manager
	private AsEditProjectManager pm;
	
	public GuiAsEditProjects() {
		super("projects.name");
	}

	@Override
	protected void init() {
		super.init();
		
		//Gets the projectmanager
		this.pm=this.module.getPjManager();
		
		//Checks if any project is loaded
		if(this.pm.isLoaded())
			this.initLoaded();
		else
			this.initUnloaded();
	}
	
	/**
	 * Init's if no project is loaded
	 */
	private void initUnloaded() {
		
		//Gets the nodes
		GuiNode nodeB = this.getInfoBox();
		GuiNode nodeSP = this.getSaveProject();
		GuiNode nodeLP = this.getLoadProject();
		
		//Calculates the space between infobox and sp/lp
		int spaceY = (int)((float)(this.height-nodeB.getHeight()-Math.max(nodeSP.getHeight(), nodeLP.getHeight())) / 3f);
		
		//Calculates the space between lp and sp
		int spaceX = (int)((float)(this.overlayWidth-nodeLP.getWidth()-nodeSP.getWidth())/3f);
		
		//Adds the boxes
		nodeB.init(this.overlayX+this.overlayWidth/2-nodeB.getWidth()/2, spaceY);
		nodeSP.init(this.overlayX+spaceX, spaceY*2+nodeB.getHeight());
		nodeLP.init(this.overlayX+spaceX*2+nodeSP.getWidth(), spaceY*2+nodeB.getHeight()+nodeSP.getHeight()/2-nodeLP.getHeight()/2);
		
		//Adds the text info holder
		this.addWidget(this.info = new TmAnimatedText(this.overlayX+this.overlayWidth/2, (int) (this.height*.05f)).setTransition(2000, 5000, 1000));
	}
	
	
	/**
	 * Node for the load project box
	 * Used by {@link #initUnloaded()}
	 */
	private GuiNode getLoadProject() {
		
		//Calculates the width and height
		final int width = (int)(this.overlayWidth*.3f);
		final int height = (int)(this.height*.2f);
		
		return new GuiNode(width,height,(x,y)->{
			//Adds the background
			this.addWidget(new TmBackgroundWidget(x, y, width, height, 0xa0000000).setOutline(0xff000000,1));
			
			//Adds the button
			this.addWidget(new TmUpdateButton(x+width/2-40, y+height/2-10, 80, 20,btn->{
				
				//Opens the project
				if(btn!=null)
					this.minecraft.displayGuiScreen(new GuiAsEditProjectsLoad());
				
				return TextUtil.getInstance().getByKey("modules.asedit.gui.projects.button.manager");
			}));
		});
		
	}
	
	/**
	 * Node for the save project box
	 * Used by {@link #initUnloaded()}
	 */
	private GuiNode getSaveProject() {
		
		//Width
		final int width = (int) (this.overlayWidth*.4f);
		
		//Height
		final int height = 160;
		
		return new GuiNode(width,height,(x,y)->{
			
			//Adds the background
			this.addWidget(new TmBackgroundWidget(x, y, width, height, 0xa0000000).setOutline(0xff000000,1));

			//Creates the name field
			this.addWidget(new TmTextWidget(
				x+10,
				y+42,
				TextUtil.getInstance().trimStringToWidth(TextUtil.getInstance().getByKey("modules.asedit.gui.projects.project.name"),width-20,"..."),
				0xffffffff
			).setAlignX(TextAlign.BEFORE));
			TmTextfield nameField = new TmTextfield(x+10, y+55, Math.min(width-20,150), 15, "",null);
			this.addWidget(nameField);
		
			//Creates the author field
			this.addWidget(new TmTextWidget(
				x+10,
				y+42+40,
				TextUtil.getInstance().trimStringToWidth(TextUtil.getInstance().getByKey("modules.asedit.gui.projects.project.author"),width-20,"..."),
				0xffffffff
			).setAlignX(TextAlign.BEFORE));
			TmTextfield authorField = new TmTextfield(x+10, y+55+40, Math.min(width-20, 120), 15, "",null);
			this.addWidget(authorField);
			
			//Creates the version field
			this.addWidget(new TmTextWidget(
				x+10,
				y+42+40*2,
				TextUtil.getInstance().trimStringToWidth(TextUtil.getInstance().getByKey("modules.asedit.gui.projects.project.version"),width-20,"..."),
				0xffffffff
			).setAlignX(TextAlign.BEFORE));
			TmTextfield versionField = new TmTextfield(x+10, y+55+40*2, Math.min(width-20, 50), 15, "",null);
			this.addWidget(versionField);
		
		
			//Adds the save project button
			this.addWidget(new TmUpdateButton(x+width/2-50,y+10,100,20,btn->{
				if(btn!=null)
					//Checks if everything is given
					if(nameField.getText().trim().isEmpty() || versionField.getText().trim().isEmpty() || authorField.getText().trim().isEmpty())
						this.info.showKey(0xFF6A6A,"modules.asedit.gui.projects.projects.info.given");
					else {
						//Gets the file
						File file = new File(Toolsmod.ID+'/'+AsEditProjectManager.SAVE_LOCATION+'/',nameField.getText()+'.'+EnumProjectVersion.NEWEST.getExtension());
						
						//Checks if the file exists
						if(file.exists())
							this.info.showKey(0xFF6A6A,"modules.asedit.gui.projects.projects.info.exists");
						else {
							//Gets the project file
							AsEditProject project = new AsEditProject(file, nameField.getText().trim(),authorField.getText().trim(), versionField.getText().trim(), EnumProjectVersion.NEWEST);
							
							//Tries to save the project
							if(this.pm.saveNewProject(project))
								this.minecraft.displayGuiScreen(null);
							else
								this.info.showKey(0xFF6A6A,"modules.asedit.gui.projects.projects.info.failed");
						}
					}				
				return TextUtil.getInstance().getByKey("modules.asedit.gui.projects.button.save");
			}));
		});
		
	}
	
	/**
	 * Node for the info box
	 * Used by {@link #initUnloaded()}
	 */
	private GuiNode getInfoBox() {
		
		//Min and max width
		final int minWidth = (int) Math.min(this.overlayWidth*.65f,250);
		final int maxWidth = (int) Math.min(this.overlayWidth*.8f,300);
		
		//Gets the text
		String infoText = TextUtil.getInstance().getByKey("modules.asedit.gui.projects.infotext");
		
		//Splits the info into lined segments
		String[] splitInfo = TextUtil.getInstance().splitStringOnWidth(infoText, minWidth, maxWidth-20).split("\n");

		return new GuiNode(maxWidth, (int)(splitInfo.length*10+5), (x,y)->{			
			//Adds the info text background
			this.addWidget(new TmBackgroundWidget(x, y, maxWidth, splitInfo.length*10+5, 0xa0000000).setOutline(0xff000000,1));
			
			//Adds the info text
			this.addWidget(new TmTextWidget(x+maxWidth/2, y+5,null,0xffffffff)
					.setText(splitInfo));
		});
	}
	
	/**
	 * Init's if a project is loaded
	 */
	private void initLoaded() {
		//Gets the nodes
		GuiNode nodeUpBox = this.getUpdateBox();
		GuiNode nodeSettings = this.getSettingsNode();
		
		//Gets the space between
		int spaceY = (this.height-nodeUpBox.getHeight()-nodeSettings.getHeight())/3;
		
		//Adds the nodes
		nodeUpBox.init(this.overlayX+this.overlayWidth/2-nodeUpBox.getWidth()/2, (int) (spaceY*1.5f));
		nodeSettings.init(this.overlayX+this.overlayWidth/2-nodeSettings.getWidth()/2, (int) (spaceY*2.25f+nodeUpBox.getHeight()));
		
		//Adds the text info holder
		this.addWidget(this.info = new TmAnimatedText(this.overlayX+this.overlayWidth/2, (int) (this.height*.05f)).setTransition(500, 5000, 500));
		this.info.setScale(2);
	}
	
	/**
	 * Node for the settings
	 * Used by {@link #initLoaded()}
	 */
	private GuiNode getSettingsNode() {
		//Size of the node
		final int w = 90;
		final int h = 60;
		
		return new GuiNode(w,h,(x,y)->{
			//Adds the background
			this.addWidget(new TmBackgroundWidget(x,y, w, h, 0xa0000000).setOutline(0xff000000,1));

			//Adds the project manager button
			this.addWidget(new TmUpdateButton(x+w/2-35, y+8, 70, 20, btn->{
				if(btn!=null)
					this.minecraft.displayGuiScreen(new GuiAsEditProjectsLoad());
				return TextUtil.getInstance().getByKey("modules.asedit.gui.projects.button.manager");
			}));
			
			//Adds unload project button
			this.addWidget(new TmUpdateButton(x+w/2-35, y+h-28, 70, 20, btn->{
				if(btn!=null) {
					//Unloads the project and adds the default stand back
					this.pm.setLoaded(null);
					this.manager.deleteAllStands();
					this.module.guis.update();
				}
				return TextUtil.getInstance().getByKey("modules.asedit.gui.projects.button.unload");
			}));
		});
	}
	
	/**
	 * Node for the update box
	 * Used by {@link #initLoaded()}
	 */
	private GuiNode getUpdateBox() {
		//Box scale
		final int width = (int)Math.min(this.overlayWidth*.9f, 300);
		final int height = 180;
		
		return new GuiNode(width,height,(x,y)->{
			//Adds the background
			this.addWidget(new TmBackgroundWidget(x, y, width, height, 0xa0000000).setOutline(0xff000000,1));
			
			//Adds the title
			this.addWidget(new TmTextWidget(x+width/2, y+5, TextUtil.getInstance().getByKey("modules.asedit.gui.projects.update.title"), 0xffffffff));
			
			//Adds the name field
			this.addWidget(new TmTextWidget(x+width/2, y+25, TextUtil.getInstance().trimStringToWidth(TextUtil.getInstance().getByKey("modules.asedit.gui.projects.project.name"), width-20, "..."), 0xffffffff));
			TmTextfield nameField = new TmTextfield(x+20, y+37, width-40,15, "",null);
			nameField.setText(this.pm.getLoaded().getName(-1));
			this.addWidget(nameField);
			
			//Adds the author-field
			this.addWidget(new TmTextWidget(x+width/2, y+25+38, TextUtil.getInstance().trimStringToWidth(TextUtil.getInstance().getByKey("modules.asedit.gui.projects.project.author"), width-20, "..."), 0xffffffff));
			//Width of the author field
			int afW = Math.min(width-40, 200);
			TmTextfield authorField = new TmTextfield(x+width/2-afW/2, y+37+38, afW, 15, "",null);
			authorField.setText(this.pm.getLoaded().getAuthor(-1));
			this.addWidget(authorField);
			
			//Adds the version-field
			this.addWidget(new TmTextWidget(x+width/2, y+25+38*2, TextUtil.getInstance().trimStringToWidth(TextUtil.getInstance().getByKey("modules.asedit.gui.projects.project.version"), width-20, "..."), 0xffffffff));
			//Width of the version field
			int vfW = Math.min(width-40, 150);
			TmTextfield versionField = new TmTextfield(x+width/2-vfW/2, y+37+38*2, vfW, 15, "",null);
			versionField.setText(this.pm.getLoaded().getVersion(-1));
			this.addWidget(versionField);
			
			//Calculates the save-button x
			int sbX = x+width/2-(this.pm.getLoaded().getVersion().isOutdated()?65:30);
			
			//Adds the save settings button
			TmUpdateButton saveBtn = new TmUpdateButton(sbX, y+37+38*3, 60, 20, btn->{
				if(btn!=null) {
					//Checks if all fields are set
					if(nameField.getText().trim().isEmpty() || authorField.getText().trim().isEmpty() || versionField.getText().trim().isEmpty()) 
						this.info.showKey(0xFF6A6A,"module.asedit.gui.projects.info.given");
					else {
						//Updates the project properties
						this.pm.getLoaded().setName(nameField.getText().trim());
						this.pm.getLoaded().setAuthor(authorField.getText().trim());
						this.pm.getLoaded().setProjectVersion(versionField.getText().trim());
						
						//Tries to save the project
						Optional<String> optError = this.pm.saveProject();
						
						if(optError.isPresent()) {
							//Displays the error
							this.info.showKey(0xFF6A6A,optError.get());
							//Reloads the project
							this.pm.getLoaded().reload();
						}else
							//Displays the saved info
							this.info.showKey(0x00E53F,"modules.asedit.gui.projects.save.success");
					}
				}
				return TextUtil.getInstance().getByKey("modules.asedit.gui.projects.button.save");
			});
			this.addWidget(saveBtn);
			
			//Checks if the version is outdated
			if(this.pm.getLoaded().getVersion().isOutdated()) {
				saveBtn.setEnabled(false);
				
				//Adds the convert button
				this.addWidget(new TmUpdateButton(x+width/2+5, y+37+38*3, 60, 20,btn->{
					if(btn!=null) {
						//Converts the project
						Optional<String> optError =  this.pm.getLoaded().convertProject();
						
						//Checks if the conversion was successful
						if(optError.isPresent()) {
							//Shows the error
							this.info.showKey(0xFF6A6A,optError.get());
						}else {
							//Reloads the project
							this.pm.loadProject(this.pm.getLoaded());
							
							//Updates the GUI
							this.module.guis.update();
						}
					}
					return TextUtil.getInstance().getByKey("modules.asedit.gui.projects.button.convert");
				}));
			}
		});
	}
}

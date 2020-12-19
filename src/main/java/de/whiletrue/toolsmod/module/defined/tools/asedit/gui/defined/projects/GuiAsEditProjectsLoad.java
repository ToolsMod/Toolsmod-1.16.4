package de.whiletrue.toolsmod.module.defined.tools.asedit.gui.defined.projects;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.whiletrue.toolsmod.gui.TmScreen;
import de.whiletrue.toolsmod.gui.widgets.TmBackgroundWidget;
import de.whiletrue.toolsmod.gui.widgets.TmTextWidget;
import de.whiletrue.toolsmod.gui.widgets.TmTextfield;
import de.whiletrue.toolsmod.gui.widgets.TmUpdateButton;
import de.whiletrue.toolsmod.gui.widgets.rounding.listmultirow.MultirowListView;
import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.module.defined.tools.ModuleAsEdit;
import de.whiletrue.toolsmod.module.defined.tools.asedit.gui.list.ProjectItem;
import de.whiletrue.toolsmod.module.defined.tools.asedit.projects.AsEditProject;
import de.whiletrue.toolsmod.module.defined.tools.asedit.projects.AsEditProjectManager;
import de.whiletrue.toolsmod.module.defined.tools.asedit.projects.EnumProjectVersion;
import de.whiletrue.toolsmod.util.classes.TextUtil;
import net.minecraft.util.Util;

public class GuiAsEditProjectsLoad extends TmScreen{

	// Directory to search
	private final String dir = Toolsmod.ID + '/'+AsEditProjectManager.SAVE_LOCATION+'/';
	
	// Reference to the module
	private ModuleAsEdit module;
	
	// Info field
	private TmTextWidget info;
	private long millis = -1;
	private TmBackgroundWidget infoBg;
	
	//List for all projects
	private MultirowListView<ProjectItem> list;
	
	@Override
	protected void init() {
		//Gets the module
		this.module=Toolsmod.getInstance().getModuleManager().getModuleByClass(ModuleAsEdit.class).get();
		
		// Calculates the list height
		int lH = (int) 35;

		// Calculates the render width
		int rW = (int) Math.min(300, this.width * .7f);

		// Calculates the starting position
		int lX = this.width - rW < 200 ? 10 : (this.width / 2 - rW / 2);

		// Creates the list
		this.addWidget(this.list=new MultirowListView<ProjectItem>(lX,lH+23,rW,
				this.height-lH-23)
				.setListFormatting(0, 5, 1, 40)
				.setScrollStrength(10)
				.setBackground(0));

		// Adds the info title
		this.addWidget(new TmTextWidget(this.width / 2, 4, TextUtil.getInstance().getByKey("modules.asedit.gui.projectsselect.title"), 0xff00CEFF).setScale(2));

		// Creates the search field
		TmTextfield search = new TmTextfield(lX, 35, rW, 15, "",i -> this.list.updateValidation());

		// Adds the search field
		this.addWidget(search);

		// Sets the list's validator
		this.list.setValidator(i -> ((ProjectItem) i).isValid(search.getText()));
		
		//Updates the list with all projects
		this.updateList();
		
		// Calculates the width of the button
		int btnW = Math.min(80, this.width - lX - rW - 10);

		// Adds the back button
		this.addWidget(new TmUpdateButton(lX + rW + (this.width - lX - rW) / 2 - btnW / 2, lH - 2, btnW, 20, btn -> {
			if (btn != null)
				this.module.guis.update();
			return TextUtil.getInstance().getByKey("modules.asedit.gui.projectsselect.btn.back");
		}));
		
		//Gets the x and y coordinates for the open folder button
		int fX = this.width - rW < 200 ? (lX+rW+(this.width-lX-rW)/2-btnW/2) : (lX-(this.width-lX-rW)/2-btnW/2);
		int fY = this.width - rW < 200 ? (lH-2-25):(lH-2);
		
		//Adds the open folder button
		this.addWidget(new TmUpdateButton(fX,fY,btnW,20,btn->{
			if(btn!=null) {
				//Gets the file
				File file = new File(this.dir);
				
				//Checks if the file exists
				if(file.exists())
					Util.getOSType().openFile(file);
			}
			return TextUtil.getInstance().getByKey("modules.asedit.gui.projectsselect.btn.folder");
		}));

		// Init's the info background
		this.infoBg = new TmBackgroundWidget((int) (this.width * .2f), (int) (this.height * .3f),
				(int) (this.width * .6f), (int) (this.height * .2f), 0xd0000000);
		this.info = new TmTextWidget(this.width / 2, (int) (this.height * .4f), "", 0xffffffff);
	}
	
	/**
	 * Removes all previous projects and adds the new
	 */
	private void updateList() {
		// Clears the list
		this.list.clearWithoutCheck();
		// Adds all projects
		for (AsEditProject proj : this.loadProjects())
			this.list.addItem(new ProjectItem(proj, this::onceLoad, this::onceDelete, this::onceUpdate));

		// Updates the list
		this.list.updateItems();
	}
	
	/**
	 * Loads all projects
	 * 
	 * @return all loaded projects
	 */
	private AsEditProject[] loadProjects() {
		//Gets the file
		File f = new File(this.dir);
		
		//Ensures that the file exists
		f.mkdirs();
		
		// For all found files
		return Arrays.stream(f.listFiles()).map(i -> {
			// Gets the loader
			Optional<EnumProjectVersion> optLoader = this.module.getPjManager().getLoaderForFile(i);
			
			// If no loader was found
			if (!optLoader.isPresent())
				return null;

			// Loads the project
			Optional<AsEditProject> optProj = optLoader.get().getLoader().loadProperties(i);

			// Returns the loaded project or if the project failed to load an invalid project
			return optProj.isPresent() ? optProj.get() : new AsEditProject.InvalidArmorStandProject(i);
		})
		// Removes all invalid files
		.filter(i -> i != null)
		// Collects them
		.toArray(AsEditProject[]::new);
	}
	
	/**
	 * Handler for the load event
	 */
	private void onceLoad(AsEditProject project) {
		//Loads the project
		if(!this.module.getPjManager().loadProject(project))
			//Displays the error
			this.displayInfo("modules.asedit.gui.projectsselect.load.failed");
		else {
			//Sends the success info
			TextUtil.getInstance().sendMessage("modules.asedit.gui.projectsselect.load.succes", project.getName(20));
			this.module.guis.update();
		}
	}

	/**
	 * Handler for the convert event
	 * @param project
	 */
	private void onceUpdate(AsEditProject project) {
		Optional<String> optError = project.convertProject();
		
		//Checks if the conversion was successful
		if(optError.isPresent()) {
			//Shows the error
			this.displayInfo(optError.get());
		}else {
			//Updates the list
			this.updateList();
		}
	}
	
	/**
	 * Handler for the delete project event
	 */
	private void onceDelete(AsEditProject project) {
		// Deletes the project
		if (!project.delete())
			//Displays the failed info
			this.displayInfo("modules.asedit.gui.projectsselect.delete.failed");
		// Updates the list
		this.updateList();
	}

	/**
	 * Displays the given info
	 * 
	 * @param key the language key
	 */
	private void displayInfo(String key) {
		// Resets the milli's
		this.millis = System.currentTimeMillis();
		this.removeWidget(this.infoBg);
		this.removeWidget(this.info);
		this.addWidget(this.infoBg);
		this.addWidget(this.info);
		this.info.setTextByKey(key);
	}
	
	@Override
	public void render(MatrixStack ms,int mouseX, int mouseY, float partialTicks) {
		// Renders the background
		this.renderDirtBackground(0);
        this.fillGradient(ms,0, 58, this.width, this.height, -1072689136, -804253680);

		// Checks if the info should no longer be displayed
		if (this.millis != -1 && this.millis + 3000 < System.currentTimeMillis()) {
			// Removes the info
			this.millis = -1;
			this.removeWidget(this.info);
			this.removeWidget(this.infoBg);
		}

		super.render(ms,mouseX, mouseY, partialTicks);
	}

}

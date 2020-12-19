package de.whiletrue.toolsmod.module.defined.tools.asedit.gui.list;

import de.whiletrue.toolsmod.gui.widgets.TmBackgroundWidget;
import de.whiletrue.toolsmod.gui.widgets.TmTextWidget;
import de.whiletrue.toolsmod.gui.widgets.TmUpdateButton;
import de.whiletrue.toolsmod.gui.widgets.rounding.listmultirow.MultirowListItem;
import de.whiletrue.toolsmod.module.defined.tools.asedit.projects.AsEditProject;
import de.whiletrue.toolsmod.module.defined.tools.asedit.projects.AsEditProject.InvalidArmorStandProject;
import de.whiletrue.toolsmod.module.defined.tools.asedit.projects.AsEditProjectManager;
import de.whiletrue.toolsmod.module.defined.tools.asedit.projects.EnumProjectVersion;
import de.whiletrue.toolsmod.util.TextAlign;
import de.whiletrue.toolsmod.util.classes.TextUtil;

public class ProjectItem extends MultirowListItem<AsEditProject>{

	//Event handler's
	private IProjectUpdate onLoad,onDelete,onConvert;
	
	//Save delete switch
	private boolean saveDelete;
	
	public ProjectItem(AsEditProject item,IProjectUpdate onLoad,IProjectUpdate onDelete,IProjectUpdate onConvert) {
		super(item);
		
		this.onLoad=onLoad;
		this.onDelete=onDelete;
		this.onConvert=onConvert;
		
		//Checks if the project is invalid
		if(item instanceof InvalidArmorStandProject) {
			//Adds the background
			this.widgets.add(new TmBackgroundWidget(0,0,0,0,0xffDE0000).setOutline(0xff000000,1));

			//Adds the info
			this.widgets.add(new TmTextWidget(0,0,TextUtil.getInstance().getByKey("modules.asedit.gui.projectsselect.item.invalid.title"),0xffffffff)
					.setAlignX(TextAlign.BEFORE));
			
			//Adds the file path
			this.widgets.add(new TmTextWidget(0,0,'\''+AsEditProjectManager.SAVE_LOCATION+'\\'+this.getItem().getFile().getName()+'\'',0xffffffff)
					.setAlignX(TextAlign.BEFORE));

			//Adds the delete button
			this.widgets.add(new TmUpdateButton(0,0,0,0,this::onDelete));
			return;
		}
		
		//Adds the background
		this.widgets.add(new TmBackgroundWidget(0,0,0,0,0).setOutline(0xff000000,1));

		//Adds the Project-name text
		this.widgets.add(new TmTextWidget(0,0, item.getName(20), 0xffffffff)
				.setAlignX(TextAlign.BEFORE));
		
		//Adds the version and author
		this.widgets.add(new TmTextWidget(0, 0, TextUtil.getInstance().getByKey("modules.asedit.gui.projectsselect.item.info",item.getVersion(5),item.getAuthor(10)), 0xffffffff)
				.setAlignX(TextAlign.AFTER)
				.setAlignX(TextAlign.BEFORE));

		//Adds the load button
		this.widgets.add(new TmUpdateButton(0, 0, 0, 0, btn->{
			if(btn!=null)
				this.onLoad.execute(this.getItem());

			return TextUtil.getInstance().getByKey("modules.asedit.gui.projectsselect.item.load");
		}));
		
		//Adds the delete button
		this.widgets.add(new TmUpdateButton(0,0,0,0,this::onDelete));
		
		//Checks if a newer version is available
		if(!EnumProjectVersion.NEWEST.equals(this.getItem().getVersion()))
			//Adds the update-version button
			this.widgets.add(new TmUpdateButton(0,0,0,0,btn->{
				if(btn!=null)
					this.onConvert.execute(this.getItem());
				return TextUtil.getInstance().getByKey("modules.asedit.gui.projectsselect.item.convert");
			}));
		
		//Updates the color
		this.handleSelect(false);
	}

	/**
	 * Delete button handler
	 */
	private String onDelete(TmUpdateButton button) {
		if(button!=null) {
			//Checks if the save delete got executed
			if(this.saveDelete)
				//Delete the project
				this.onDelete.execute(this.getItem());
			else {
				this.saveDelete=true;
				return TextUtil.getInstance().getByKey("modules.asedit.gui.projectsselect.item.delete.save");
			}
		}
		return TextUtil.getInstance().getByKey("modules.asedit.gui.projectsselect.item.delete");
	}
	
	@Override
	public void handlChangePosition(int x, int y, int w, int h) {
		super.handlChangePosition(x, y, w, h);
		
		if(this.getItem() instanceof InvalidArmorStandProject) {
			//Background
			this.widgets.get(0).move(x,y,width,height);
			
			//Info
			this.widgets.get(1).move(x+10,y+4);
			
			//Path
			this.widgets.get(2).move(x+10,y+height-12);
			
			//Delete button
			this.widgets.get(3).move(x+width-50,y+10,45,20);
			return;
		}
		
		//Background
		this.widgets.get(0).move(x,y,width,height);
		
		//Project-name
		this.widgets.get(1).move(x+10,y+4);
		//Project-version and author
		this.widgets.get(2).move(x+10,y+height-10);
		
		//Delete button
		this.widgets.get(3).move(x+width-100,y+10,45,20);
		//Load button
		this.widgets.get(4).move(x+width-50,y+10,45,20);
		
		//Checks if a newer version is available
		if(!EnumProjectVersion.NEWEST.equals(this.getItem().getVersion()))
			//Update button
			this.widgets.get(5).move(x+width-150,y+10,45,20);
	}
	
	/**
	 * Validates the current item against the given keyword
	 */
	public boolean isValid(String keyword) {
		//Checks if the item is invalid
		if(this.getItem() instanceof InvalidArmorStandProject)
			return this.getItem().getFile().getName().toLowerCase().contains(keyword);
		return this.getItem().getName(-1).toLowerCase().contains(keyword);
	}
	
	/**
	 * Executor for the select or unselect event
	 */
	public void handleSelect(boolean selected) {
		//Updates the background
		((TmBackgroundWidget)this.widgets.get(0)).setColor(selected?0xff0861B3:0xff505050);
	}
	
	@FunctionalInterface
	public interface IProjectUpdate{
		public void execute(AsEditProject project);
	}
}

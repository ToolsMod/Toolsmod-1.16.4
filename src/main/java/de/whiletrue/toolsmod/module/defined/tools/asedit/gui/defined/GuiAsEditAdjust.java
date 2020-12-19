package de.whiletrue.toolsmod.module.defined.tools.asedit.gui.defined;

import de.whiletrue.toolsmod.gui.widgets.TmBackgroundWidget;
import de.whiletrue.toolsmod.gui.widgets.TmItemButton;
import de.whiletrue.toolsmod.gui.widgets.TmSlider;
import de.whiletrue.toolsmod.gui.widgets.TmTextWidget;
import de.whiletrue.toolsmod.gui.widgets.TmTextfield;
import de.whiletrue.toolsmod.gui.widgets.TmUpdateButton;
import de.whiletrue.toolsmod.gui.widgets.preset.TmWidget;
import de.whiletrue.toolsmod.gui.widgets.rounding.GuiNode;
import de.whiletrue.toolsmod.module.defined.tools.asedit.EnumMoveType;
import de.whiletrue.toolsmod.module.defined.tools.asedit.gui.GuiAsEdit;
import de.whiletrue.toolsmod.util.classes.JavaUtil;
import de.whiletrue.toolsmod.util.classes.TextUtil;
import net.minecraft.item.Items;
import net.minecraft.util.math.vector.Vector3d;

public class GuiAsEditAdjust extends GuiAsEdit{

	public GuiAsEditAdjust() {
		super("adjust.name");
	}

	@Override
	protected void init() {
		super.init();

		//Checks if there is enoguth space for the list
		if(this.overlayWidth>350)
			// Adds the list
			this.appendList();

		// Checks if multiple stands are selected
		if (this.manager.getSelectedStand() == null)
			this.initMulti();
		else
			this.initSingle();
	}
	
	/**
	 * Init for a single stand
	 */
	private void initSingle() {
		// If the mode is rotation
		boolean isRot = EnumMoveType.ROTATION.equals(this.manager.getMode());
		boolean isUnset = this.manager.getMode() == null;

		//Gets the nodes
		GuiNode modeNode = this.addModeButtons();
		GuiNode axisNode = this.addAxisButtons(isRot?1:3);
		GuiNode speedNode = this.addSpeedButtons();
		
		// Space between the modules
		int spaceY = (this.height-modeNode.getHeight() - (isUnset?0:axisNode.getHeight()+speedNode.getHeight())) / (isUnset?2:4);

		// Adds the mode-node
		modeNode.init(this.overlayX+this.overlayWidth/2-modeNode.getWidth()/2, spaceY);

		// Checks if the mode is set
		if (!isUnset) {
			// Adds the axis node
			axisNode.init(this.overlayX+this.overlayWidth/2-axisNode.getWidth()/2, spaceY*2+modeNode.getHeight());
			// Adds the speed node
			speedNode.init(this.overlayX+this.overlayWidth/2-speedNode.getWidth()/2, spaceY*3+modeNode.getHeight()+axisNode.getHeight());
		}
	}
	
	/**
	 * Node for the axis buttons
	 * @param axisAmount the amount of axis that will be added
	 * Used by {@link #initSingle()}
	 */
	private GuiNode addAxisButtons(int axisAmount) {

		// Axis names
		char axis[] = { 'X', 'Y', 'Z' };

		// Size of the node
		final int w = Math.min(200, (int) (this.overlayWidth * .9f));
		final int h = axisAmount * 25 + 20;

		//Difference between the buttons in heights
		final int btnDiffH = (h-20)/3;
		
		return new GuiNode(w,h,(x,y)->{
			// Adds the background
			this.addWidget(new TmBackgroundWidget(x, y, w, h, 0xa0000000).setOutline(0xff000000,1));

			// Adds the info-text
			this.addWidget(new TmTextWidget(x + w / 2, y + 5,
					TextUtil.getInstance().getByKey("modules.asedit.gui.adjust.adjust"), 0xffffffff));
			
			// Checks if the mode is the rotation
			if (this.manager.getMode().equals(EnumMoveType.ROTATION)) {
				
				//Editor widget
				TmWidget editor = null;
				
				//Checks which widget should be used
				if(!this.module.sTextRotation.value) {
					//Adds the rotation slider
					editor = new TmSlider(0, 0, 0, 0, 0, 360, this.manager.getSelectedStand().prevRotationYaw, (slider,value)->{
						this.manager.getSelectedStand().rotationYaw=value;
						return (Math.round(value*100)/100f)+" °";
					});
				}else {
					// Adds the rotation field
					editor = new TmTextfield(0,0,0,0, "",txt -> {
						// Updates the position
						if (JavaUtil.getInstance().isFloat(txt))
							this.manager.getSelectedStand().rotationYaw = Float.valueOf(txt);
					})
					.setText(String.valueOf(this.manager.getSelectedStand().rotationYaw))//Sets the text
					.setValidator(txt -> txt.matches("\\d*\\.?\\d*"));//Sets the validator
				}
				
				//Moves the editor
				editor.move(x+5, y + 23, w-10, 14);
				
				// Adds the editor
				this.addWidget(editor);
				return;
			}
			// For every axis
			for (int i = 0; i < axis.length; i++) {
				// Wrapper
				final int wrapper = i;
				
				// Adds the button
				this.addWidget(new TmUpdateButton(x+5, y + i * btnDiffH + 20, 20, 20, btn -> {
					if (btn != null) {
						// Sets the new axis
						this.manager.setAxis(wrapper);
						// Updates the GUI
						this.module.guis.update();
					}
					return String.valueOf(axis[wrapper]);
				}).setEnabled(i != this.manager.getAxis()));
				
				// Gets the rotations
				Vector3d rots = this.manager.getGlobalRotations();
				//Gets the current rotation
				double current = i == 0 ? rots.getX() : i == 1 ? rots.getY() : rots.getZ();

				//Selected editor
				TmWidget editor = null;
				
				//Checks which type should be used
				if(!this.module.sTextRotation.value && !this.manager.getMode().equals(EnumMoveType.POSITION)) {
					//%360 for negative values
					while(current<0)
						current+=360;
					
					//Creates the axis slider
					editor = new TmSlider(0, 0, 0, 0, 0, 360, (float) current, (slider,value)->{
						this.manager.setGlobalRotation(wrapper, value);
						return (Math.round(value*100)/100f)+" °";
					});
				}else {

					// Creates the axis-field
					editor = new TmTextfield(0,0,0,0, "",txt -> {
						// Updates the position
						if (JavaUtil.getInstance().isFloat(txt))
							this.manager.setGlobalRotation(wrapper, Float.valueOf(txt));
					})
					.setText(String.valueOf(current))//Sets the text
					.setValidator(txt -> txt.matches("-?\\d*\\.?\\d*"));//Sets the validator
				}
				
				//Updates the position
				editor.move(x + 30, y + i * btnDiffH + 23, w - 35, 14);
				
				// Adds the editor
				this.addWidget(editor);
			}
		});
	}
	
	/**
	 * Node for the mode buttons
	 * Used by {@link #initSingle()}
	 */
	private GuiNode addModeButtons() {
		// Mode-button sizes
		final int btnW = 40;
		final int btnH = 20;
		final int spaceX = Math.min(20,(this.overlayWidth - btnW * EnumMoveType.values().length / 2 - 30) / (EnumMoveType.values().length - 1));

		//Size of the field
		final int w = EnumMoveType.values().length / 2 * (btnW + spaceX) + 25;
		final int h = btnH*2+30;
		
		return new GuiNode(w,h,(x,y)->{
			// Adds the background
			this.addWidget(new TmBackgroundWidget(x, y, w, 70, 0xa0000000).setOutline(0xff000000,1));
			
			// Adds the info-text
			this.addWidget(new TmTextWidget(x+w/2, y + 6,
					TextUtil.getInstance().getByKey("modules.asedit.gui.adjust.mode"), 0xffffffff));
			
			// Adds the mode's
			for (int i = 0; i < EnumMoveType.values().length; i++) {
				// Gets the mode
				EnumMoveType mode = EnumMoveType.values()[i];
				
				// Adds the button
				this.addWidget(new TmUpdateButton(x + spaceX / 2 + (btnW + spaceX) * (i / 2),
						y + (i % 2 == 1 ? (btnH + 5) : 0) + 20, btnW, btnH, btn -> {
							if (btn != null) {
								// Sets the new mode
								this.manager.setMode(mode);
								// Updates the GUI
								this.module.guis.update();
							}
							return mode.getShortName();
						}).setEnabled(!mode.equals(this.manager.getMode())));
			}
			// Adds the de-select button
			this.addWidget(new TmItemButton(x + w - 25, y + 20, Items.BARRIER, btn -> {
				// Unselect's anything
				this.manager.setMode(null);
				//Updates the GUI
				this.module.guis.update();
			}).setEnabled(this.manager.getMode() != null).setTooltippByKey("modules.asedit.gui.adjust.mode.none"));
		});
	}
	
	/**
	 * Init for multiple stands
	 */
	private void initMulti() {
		// If the mode is rotation
		boolean isRot = EnumMoveType.ROTATION.equals(this.manager.getMode());
		boolean isUnset = this.manager.getMode() == null;

		//Gets the nodes
		GuiNode modeNode = this.addMultiModeButtons();
		GuiNode axisNode = this.addMultiAxisButtons();
		GuiNode speedNode = this.addSpeedButtons();
		
		// Space between the modules
		int spaceY = (this.height - modeNode.getHeight() -(isRot||isUnset?0:axisNode.getHeight()) -(isUnset?0:speedNode.getHeight()))/(isUnset?2:isRot?3:4);
		
		// Adds the mode-node
		modeNode.init(this.overlayX+this.overlayWidth/2-modeNode.getWidth()/2, spaceY);

		// Checks if the mode is set
		if (!isUnset) {
			// Adds the axis node
			if (!isRot)
				axisNode.init(this.overlayX+this.overlayWidth/2-axisNode.getWidth()/2, spaceY*2+modeNode.getHeight());
			// Adds the speed node
			speedNode.init(this.overlayX+this.overlayWidth/2-speedNode.getWidth()/2, spaceY*(isRot?2:3)+(isRot?0:axisNode.getHeight())+modeNode.getHeight());
		}
	}
	
	/**
	 * Node for the speed buttons
	 * Used by {@link #initMulti()} and {@link #initSingle()}
	 */
	private GuiNode addSpeedButtons() {
		// Speed's
		float[] speed = { 10, 1, .1f };

		//Properties of the buttons
		final int btnW = 30;
		final int btnH = 20;
		final int spaceX = 40;

		// Size of the node
		final int w = (btnW + spaceX) * speed.length - spaceX;
		final int h = btnH+25;
		
		return new GuiNode(w,h,(x,y)->{			
			// Adds the background
			this.addWidget(new TmBackgroundWidget(x, y, w, h, 0xa0000000).setOutline(0xff000000,1));

			// Adds the info-text
			this.addWidget(new TmTextWidget(x + w / 2, y + 5,
					TextUtil.getInstance().getByKey("modules.asedit.gui.adjust.speed"), 0xffffffff));
			
			// For all speeds
			for (int i = 0; i < speed.length; i++) {
				// Wrapper
				int wrapper = i;
				
				// Adds the button
				this.addWidget(new TmUpdateButton(x + i * (btnW + spaceX-5)+5, y + 20, btnW, btnH, btn -> {
					if (btn != null) {
						// Sets the new speed
						this.manager.setSpeed(speed[wrapper]);
						// Closes the GUI
						this.module.guis.update();
					}
					return String.valueOf(speed[wrapper]);
				}).setEnabled(this.manager.getSpeed() != speed[i]));
			}
		});
	}
	
	/**
	 * Node for the multi axis buttons
	 * Used by {@link #initMulti()}
	 */
	private GuiNode addMultiAxisButtons() {
		// Size
		final int btn = 20;
		final int space = 30;

		final int w = space * 2 + btn * 3 + 10;
		final int h = 45;
		
		// Axis
		char axis[] = new char[] { 'X', 'Y', 'Z' };

		return new GuiNode(w,h,(x,y)->{
			// Adds the overlay
			this.addWidget(new TmBackgroundWidget(x, y, w, h, 0xa0000000).setOutline(0xff000000,1));
			// Adds the info-text
			this.addWidget(new TmTextWidget(x+w/2, y + 5,
					TextUtil.getInstance().getByKey("modules.asedit.gui.adjust.adjust"), 0xffffffff));
			// For every axis
			for (int i = 0; i < axis.length; i++) {
				// Wrapper
				int wrapper = i;
				//Create the button
				TmUpdateButton button = new TmUpdateButton(x + 5 + (btn + space) * i, y + 20, btn, btn, b -> {
					if (b != null) {
						// Updates the manager
						this.manager.setAxis(wrapper);
						// Closes the GUI
						this.module.guis.update();
					}
					return String.valueOf(axis[wrapper]);
				});
				button.setEnabled(this.manager.getAxis()!=i);
				
				// Adds the button
				this.addWidget(button);
			}
		});
	}
	
	/**
	 * Node for the multi mode buttons
	 * Used by {@link #initMulti()}
	 */
	private GuiNode addMultiModeButtons() {
		// Button properties
		final int btnWidth = 80;
		final int spaceX = 10;
		
		//Size of the node
		final int w = btnWidth * 2 + 20 + spaceX * 4;
		final int h = 45;

		return new GuiNode(w,h,(x,y)->{			
			// Adds the background
			this.addWidget(new TmBackgroundWidget(x, y, w, h, 0xa0000000).setOutline(0xff000000,1));
			// Adds the info-text
			this.addWidget(new TmTextWidget(x+w/2, y + 6,
					TextUtil.getInstance().getByKey("modules.asedit.gui.adjust.mode"), 0xffffffff));
			// Creates the buttons
			for (int i = 0; i < 2; i++) {
				// Gets the move-type
				EnumMoveType move = EnumMoveType.values()[i + 6];
				
				// Adds the button
				this.addWidget(new TmUpdateButton(x + spaceX + (btnWidth + spaceX) * i, y + 20, btnWidth, 20, btn -> {
					if (btn != null) {
						// Updates the manager
						this.manager.setMode(move);
						// Updates the GUI
						this.module.guis.update();
					}
					return move.getName();
				}).setEnabled(!move.equals(this.manager.getMode())));
			}
			// Adds the unselect button
			this.addWidget(new TmItemButton(x + spaceX * 3 + btnWidth * 2, y + 20, Items.BARRIER, btn -> {
				// Unselects anything
				this.manager.setMode(null);
				// Updates the GUI
				this.module.guis.update();
			}).setEnabled(this.manager.getMode() != null).setTooltippByKey("modules.asedit.gui.adjust.none"));
		});
	}
	
	@Override
	protected void handleTeleport() {
		super.handleTeleport();
		
		//Checks if the position is selected
		if(this.manager.getMode().equals(EnumMoveType.POSITION))
			//Updates the gui
			this.module.guis.update();
	}
}

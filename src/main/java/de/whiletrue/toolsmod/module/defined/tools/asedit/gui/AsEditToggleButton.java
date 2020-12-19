package de.whiletrue.toolsmod.module.defined.tools.asedit.gui;

import de.whiletrue.toolsmod.module.defined.tools.asedit.EditableArmorStand;

public class AsEditToggleButton {

	private final String name;
	private final IGet get;
	private final ISet set;

	public AsEditToggleButton(IGet get, ISet set, String name) {
		this.get = get;
		this.set = set;
		this.name = name;
	}

	public void reverse(EditableArmorStand stand) {
		this.set(stand, !this.get(stand));
	}

	public void set(EditableArmorStand stand, boolean set) {
		this.set.set(stand, set);
	}

	public boolean get(EditableArmorStand stand) {
		return this.get.get(stand);
	}

	public String getName() {
		return this.name;
	}
	
	@FunctionalInterface
	public interface ISet {
		public void set(EditableArmorStand stand, boolean set);
	}

	@FunctionalInterface
	public interface IGet {
		public boolean get(EditableArmorStand stand);
	}
}

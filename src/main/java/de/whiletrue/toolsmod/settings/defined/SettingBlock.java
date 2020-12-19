package de.whiletrue.toolsmod.settings.defined;

import java.util.Optional;

import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.settings.Setting;
import de.whiletrue.toolsmod.settings.views.SettingView;
import de.whiletrue.toolsmod.settings.views.SettingViewBlock;
import de.whiletrue.toolsmod.util.classes.ItemUtil;
import net.minecraft.block.Block;

public class SettingBlock extends Setting<Block>{

	@Override
	public String handleSave() {
		return super.value.getTranslationKey().substring(6).replace(".", ":");
	}

	@Override
	public boolean handleParse(String value) {
		//Loads the block from its name
		Optional<Block> parse = ItemUtil.getInstance().getBlockFromName(value);
		
		//Updates the value
		if(parse.isPresent()) {
			this.value=parse.get();
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X extends Setting<Block>> SettingView<X> getView(Module mod) {
		return (SettingView<X>) new SettingViewBlock(this, mod);
	}
}

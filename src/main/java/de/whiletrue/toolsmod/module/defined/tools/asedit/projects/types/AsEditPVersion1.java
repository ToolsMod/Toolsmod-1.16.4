package de.whiletrue.toolsmod.module.defined.tools.asedit.projects.types;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.StreamSupport;

import com.google.gson.JsonArray;

import de.whiletrue.toolsmod.module.defined.tools.asedit.EditableArmorStand;
import de.whiletrue.toolsmod.module.defined.tools.asedit.projects.AsEditProject;
import de.whiletrue.toolsmod.module.defined.tools.asedit.projects.EnumProjectVersion;
import de.whiletrue.toolsmod.util.classes.FileUtil;
import de.whiletrue.toolsmod.util.classes.ItemUtil;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;

public class AsEditPVersion1 implements IProjectVersion{

	@Override
	public Optional<AsEditProject> loadProperties(File file) {
		//Gets the project-name
		String name = file.getName();
		
		//Loads the projects as old
		return Optional.of(new AsEditProject(file, name.substring(0,name.length()-5),"Unknown","v1",EnumProjectVersion.JSON_VERSION));
	}

	@Override
	public Optional<EditableArmorStand[]> loadProject(File file, BlockPos pos) {
		try {
			//Loads the stands
			JsonArray arr = FileUtil.getInstance().loadFileAsJson(file.getAbsolutePath()).get().getAsJsonArray();
			
			//Converts the array to a stream
    		return Optional.of(StreamSupport.stream(arr.spliterator(), false)
    		//Maps them to theirs stands
    		.map(i->{
    			//Creates the stand
    			EditableArmorStand as = new EditableArmorStand(0,0,0);
    			
    			//Parses the NBT
    			CompoundNBT nbt = ItemUtil.getInstance().stringToNbt(i.getAsString()).get();
    			//Writes the NBT to the stand
    			as.read(nbt);
    			
    			//Gets the relative position
    			ListNBT realtive = nbt.getList("Pos", 6);
    			//Sets the real position
    			as.setPosition(pos.getX()+realtive.getDouble(0),
					pos.getY()+realtive.getDouble(1),
					pos.getZ()+realtive.getDouble(2)
				);
    			
    			//Returns the stack
    			return as;
    		})
    		//Collects them
    		.toArray(EditableArmorStand[]::new));
		}catch(Exception e) {
			return Optional.empty();
		}
	}

	@Override
	public boolean saveProject(AsEditProject project, EditableArmorStand[] stands, File file, BlockPos relative) {
		//Checks if the file already exists
			if(file.exists())
				return false;
			
			//Creates the output array
	    	JsonArray out = new JsonArray();
	    	
			//For all stands
	    	Arrays.stream(stands).forEach(i->{
				//Gets the NBT-tag
				CompoundNBT nbt = ItemUtil.getInstance().getTagFromArmorstand((ArmorStandEntity) i);
				{
					//Sets the relative position
					ListNBT posList = new ListNBT();
					posList.add(DoubleNBT.valueOf(i.getPosX()-relative.getX()));
					posList.add(DoubleNBT.valueOf(i.getPosY()-relative.getY()));
					posList.add(DoubleNBT.valueOf(i.getPosZ()-relative.getZ()));
					nbt.put("Pos", posList);
				}

				//Appends the next stand
				out.add(nbt.toString());
	    	});
	    	
	    	//Outputs the file
	    	FileUtil.getInstance().printToFile(file.getAbsolutePath(), out);
	    	
	    	return true;
	}

}

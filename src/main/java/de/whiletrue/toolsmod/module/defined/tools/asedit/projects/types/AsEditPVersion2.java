package de.whiletrue.toolsmod.module.defined.tools.asedit.projects.types;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.StreamSupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

public class AsEditPVersion2 implements IProjectVersion{

	@Override
	public Optional<AsEditProject> loadProperties(File file) {
		//Loads the properties
		Optional<String[]> optLines=FileUtil.getInstance().readLines(file.getAbsolutePath(), 3);
		
		//Checks if anything went wrong
		if(!optLines.isPresent())
			return Optional.empty();
		
		//Gets the lines
		String[] lines = optLines.get();
		
		//Returns the loaded properties
		return Optional.of(new AsEditProject(file, lines[0], lines[1],lines[2], EnumProjectVersion.LINE_VERSION));
	}

	@Override
	public Optional<EditableArmorStand[]> loadProject(File file, BlockPos pos) {
		try {
			//Loads the file
			String cont = FileUtil.getInstance().loadFile(file.getAbsolutePath()).get();
			
			//Run variable to find the start
			int y=0;
			for(int i=0;i<3;i++)
				y=cont.indexOf('\n',y+1);
			
			//Gets the json values
			JsonArray arr = new JsonParser().parse(cont.substring(y+1)).getAsJsonArray();
			
			//Converts the array to a stream
    		return Optional.of(StreamSupport.stream(arr.spliterator(), false)
    		//Maps them to theirs stands
    		.map(i->{
    			//Gets the object
    			JsonObject obj = i.getAsJsonObject();
    			
    			//Creates the stand
    			EditableArmorStand as = new EditableArmorStand(0,0,0);
    			
    			//Parses the NBT
    			CompoundNBT nbt = ItemUtil.getInstance().stringToNbt(obj.get("nbt").getAsString()).get();
    			//Writes the NBT to the stand
    			as.read(nbt);
    			
    			//Updates the reference name
    			as.setReferenceName(obj.get("name").getAsString());
    			
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
		//Output string
		String outPut = String.format("%s\n%s\n%s\n",project.getName(-1),project.getAuthor(-1),project.getVersion(-1));
		
		//Creates the output array
		JsonArray out = new JsonArray();
    	
		//For all stands
    	Arrays.stream(stands).forEach(i->{
    		//Creates the wrapper with the name and nbt
    		JsonObject wrapper = new JsonObject();
    		{
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
    			
    			//Appends the stands name
    			wrapper.addProperty("name", i.getReferenceName());
    			//Appends the nbt
    			wrapper.addProperty("nbt", nbt.toString());
    		}
    		
			//Appends the next stand
    		out.add(wrapper);
    	});
    	
    	//Appends the stands to the file
    	outPut+=out.toString();
    	
    	//Outputs the file
    	FileUtil.getInstance().printToFile(file.getAbsolutePath(), outPut);
    	return true;
	}

}

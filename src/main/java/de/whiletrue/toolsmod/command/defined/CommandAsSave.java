package de.whiletrue.toolsmod.command.defined;

import java.util.stream.StreamSupport;

import de.whiletrue.toolsmod.command.ArgsReturn;
import de.whiletrue.toolsmod.command.Arguments;
import de.whiletrue.toolsmod.command.CommandError;
import de.whiletrue.toolsmod.module.defined.tools.asedit.EditableArmorStand;
import de.whiletrue.toolsmod.module.defined.tools.asedit.projects.AsEditProject;
import de.whiletrue.toolsmod.util.classes.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.util.math.BlockPos;

public class CommandAsSave extends Command {

	public CommandAsSave() {
		super("AsSave");
	}

	@Override
	public CommandError execute(Arguments args) {

		// Gets the first position
		ArgsReturn<BlockPos> pos = args.nextPosition();

		// Checks if the first position wasn't parsed
		if (pos.hasArgumentError())
			return this.getSyntaxError();

		// Checks if the position couldn't be parsed
		if (pos.hasParseError())
			return CommandError.of("command.assave.error.pos");

		// Gets the second position
		ArgsReturn<BlockPos> pos2 = args.nextPosition();

		// Checks if the first position wasn't parsed
		if (pos.hasArgumentError())
			return CommandError.of("command.assave.error.pos2");

		// Checks if the position couldn't be parsed
		if (pos.hasParseError())
			return CommandError.of("command.assave.error.pos");

		// Gets the name
		ArgsReturn<String> name = args.nextString();

		// Checks if the name wan't given
		if (name.hasArgumentError())
			return CommandError.of("command.assave.error.name");

		// Tries to create save the region
		return this.saveStands(pos.get(), pos2.get(), name.get());
	}

	/**
	 * Saves the given armorstands of a region to a file
	 * 
	 * @param pos1
	 * @param pos2
	 * @param name
	 * @return optionally an error if anything went wrong
	 */
	private CommandError saveStands(BlockPos pos1, BlockPos pos2, String name) {
		// Checks if the name is valid
		if (!name.matches("\\w+"))
			return CommandError.of("command.assave.error.name.invalid", name);

		// Gets the region
		BlockPos[] region = this.getSizedRegion(pos1, pos2);

		// Gets the size of the region
		int sizeX = region[1].getX() - region[0].getX();
		int sizeY = region[1].getY() - region[0].getY();
		int sizeZ = region[1].getZ() - region[0].getZ();

		// Checks if the region is to big
		if (sizeX > 40 || sizeY > 40 || sizeZ > 40)
			return CommandError.of("command.assave.error.size", sizeX, sizeY, sizeZ);

		// Distance of the player to the center
		double dist = region[0].add(sizeX / 2, sizeY / 2, sizeZ / 2)
				.distanceSq(Minecraft.getInstance().player.getPosition());

		// Checks if the user is to far of
		if (dist > 20)
			return CommandError.of("command.assave.error.dist");

		// Creates the project
		AsEditProject project = new AsEditProject(name, name);

		// Checks if the name already exists
		if (project.getFile().exists())
			return CommandError.of("command.assave.error.file",project.getName(-1));

		// Gets all stand from the region
		EditableArmorStand[] stands = this.convertStandsToEditStands(region);

		// Checks if any stands could be found
		if (stands.length <= 0)
			return CommandError.of("command.assave.error.empty");

		//Saves the project
		project.saveProject(stands);

		// Sends the info
		TextUtil.getInstance().sendMessage("command.assave.success", stands.length, project.getName(-1));

		return CommandError.none();
	}

	/**
	 * Converts all armor stands in the given region to editstands
	 * @param region the region
	 * @return all converted stands
	 */
	private EditableArmorStand[] convertStandsToEditStands(BlockPos[] region) {
		// Gets all entity
		return StreamSupport.stream(Minecraft.getInstance().world.getAllEntities().spliterator(), false)
				// Filters the armor stands
				.filter(i -> i instanceof ArmorStandEntity && !(i instanceof EditableArmorStand))
				// Filters for the location
				.filter(p -> p.getPosX() >= region[0].getX() || p.getPosX() <= region[1].getX()
						|| p.getPosY() >= region[0].getY() || p.getPosY() <= region[1].getY()
						|| p.getPosZ() >= region[0].getZ() || p.getPosZ() <= region[1].getZ())
				// Clones the editable armor stand
				.map(i -> new EditableArmorStand((ArmorStandEntity) i))
				// Collects all stands
				.toArray(EditableArmorStand[]::new);
	}

	/**
	 * Converts 2 positions into a region
	 * 
	 * @param pos1
	 *            first position
	 * @param pos2
	 *            second position
	 * @return the biggest and smallest position
	 */
	private BlockPos[] getSizedRegion(BlockPos pos1, BlockPos pos2) {
		return new BlockPos[] {
				new BlockPos(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()),
						Math.min(pos1.getZ(), pos2.getZ())),
				new BlockPos(Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()),
						Math.max(pos1.getZ(), pos2.getZ())) };
	}

}

package de.whiletrue.toolsmod.command.defined;

import de.whiletrue.toolsmod.command.ArgsReturn;
import de.whiletrue.toolsmod.command.Arguments;
import de.whiletrue.toolsmod.command.CommandError;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.BlockPos;

public class CommandYClip extends Command{

	public CommandYClip() {
		super("YClip","vclip");
	}
	
	@Override
	public CommandError execute(Arguments args) {
		//Creates an reference to the minecraft instance
        Minecraft mc = Minecraft.getInstance();

        ArgsReturn<Integer> optHigh = args.nextInteger();
        //Checks if the high is given
        if(optHigh.hasArgumentError())
        	return this.getSyntaxError();

        //Checks if the high could be parsed
        if(optHigh.hasParseError())
            return CommandError.of("command.yclip.error.high");

        //Teleport's to that high
        BlockPos pos = mc.player.getPosition().add(0,optHigh.get(),0);
        mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(pos.getX(), pos.getY(), pos.getZ(), mc.player.isOnGround()));
        mc.player.setPosition(pos.getX(), pos.getY(), pos.getZ());

        //Exits with no errors
        return CommandError.none();
	}

}

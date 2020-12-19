package de.whiletrue.toolsmod.command;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lwjgl.glfw.GLFW;

import de.whiletrue.toolsmod.mod.Toolsmod;
import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.util.classes.ItemUtil;
import de.whiletrue.toolsmod.util.classes.JavaUtil;
import de.whiletrue.toolsmod.util.classes.ReflectionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

public class Arguments {

	//Raw arguments
	private String[] args;

	//Reference to the game
	private Minecraft mc = Minecraft.getInstance();
	
	/**
	 * Creates an arguments object
	 *
	 * @param args the raw arguments
	 * @param mc the reference to the game instance
	 * */
	private Arguments(String[] args) {
		this.args = args;
	}
	
	/**
	 * Creates a new instance of arguments
	 *
	 * @param args the raw arguments
	 */
	public static Arguments of(String... args) {
		return new Arguments(args);
	}
	
	/**
	 * Gets an argument
	 */
	private Optional<String> pop(){
		//Checks if the arguments contains at least one argument
		if(this.args.length<=0)
			return Optional.empty();
		//Gets the first from the arguments
		Optional<String> opt = Optional.ofNullable(args[0]);
		//Removes the first index from the array
		this.args = Arrays.copyOfRange(this.args, 1, this.args.length);
		//Returns the given argument
		return opt;
	}
	
	/**
	 * Gets all arguments that are left
	 */
	private Optional<String> popLeft(){
		//Joins all arguments that are left to one string
		String left = Arrays.stream(this.args).collect(Collectors.joining(" "));
		//Return empty if the string is empty or the string
		return left.isEmpty()?Optional.empty():Optional.of(left);
	}
	
	/**
	 * Pushes the given arguments to the array
	 *
	 * @param arguments the arguments
	 */
	private void push(Optional<String>... arguments) {
		this.args = Stream.concat(
				Arrays.stream(arguments).filter(i->i.isPresent()).map(i->i.get()),
				Arrays.stream(this.args))
				.toArray(String[]::new);
	}
	
	/**
	 * Default method to parse the different
	 */
	private<T> ArgsReturn<T> parser(IParser<T> ex) {
		//Gets the string
		Optional<String> pop = this.pop();
		//Checks if its given
		if(!pop.isPresent()) {
			this.push(pop);
			return new ArgsReturn<T>();
		}
		//Try's to parse the value
		Optional<T> get = ex.exec(pop.get());
		//Checks if that worked
		if(!get.isPresent()) {
			this.push(pop);
			return new ArgsReturn<T>(pop.get());
		}
		//Returns that value
		return new ArgsReturn<T>(get.get(),pop.get());
	}

	/**
	 * Gets a string of the given allowed strings
	 *
	 * @param allowed the allowed strings
	 * */
	public ArgsReturn<String> nextOf(String[] allowed){
		return this.parser(i->{
			//Checks if the allowed contain the string
			for(String s:allowed)
				if(s.equalsIgnoreCase(i))
					return Optional.of(i);

			//Returns the parse error
			return Optional.empty();
		});
	}

	/**
	 * Gets a string 
	 */
	public ArgsReturn<String> nextString(){
		return this.parser(Optional::of);
	}
	
	/**
	 * Gets all argument that are left as a string
	 */
	public ArgsReturn<String> nextStringleft(){
		//Try's to get a string from the arguments
		Optional<String> left = this.popLeft();
		//Checks if the string is empty
		if(!left.isPresent()) {
			this.push(left);
			return new ArgsReturn<>();
		}
		return new ArgsReturn<>(left.get(),left.get());
	}
	
	/**
	 * Gets a position from x y z
	 */
	public ArgsReturn<BlockPos> nextPosition() {
		//Try's to get 3 string from the arguments
		Optional<String> sx = this.pop();
		Optional<String> sy = this.pop();
		Optional<String> sz = this.pop();
		
		//Checks if there are at least 3 string contained in the arguments
		if(!sx.isPresent()||!sy.isPresent()||!sz.isPresent()) {
			this.push(sx,sy,sz);
			return new ArgsReturn<>();
		}

		//Try's to convert them into position data
		Optional<Double> optX = this.getPosFromString(sx.get(), this.mc.player.getPosX());
		Optional<Double> optY = this.getPosFromString(sy.get(), this.mc.player.getPosY());
		Optional<Double> optZ = this.getPosFromString(sz.get(), this.mc.player.getPosZ());
		
		//Checks if the worked
		if(!optX.isPresent()||!optY.isPresent()||!optZ.isPresent()) {
			this.push(sx,sy,sz);
			return new ArgsReturn<>(String.format("%s %s %s",sx.get(),sy.get(),sz.get()));
		}
			
		//Executes the given instruction
		return new ArgsReturn<>(new BlockPos(optX.get(),optY.get(),optZ.get()),sx.get()+" "+sy.get()+" "+sz.get());
	}
	/**
	 * Used by position function to a single position value by a given string
	 *
	 * @param name the string that should be a position
	 * @param edge the default return if the name is empty
	 */
	private Optional<Double> getPosFromString(String name,double edge) {
		//Try's to convert the given string to a double
		if(JavaUtil.getInstance().isDouble(name))
			return Optional.of(Double.valueOf(name));
		//Looks if the string otherwise starts with a relative coord
		if(!name.startsWith("~"))
			return Optional.empty();
		//Removes the relative coord from the string
		name=name.substring(1);
		//Checks if any position data has been given 
		if(name.isEmpty())
			//If not if just returns the players coord
			return Optional.of(edge);
		//Checks if the position data can be converted into an double
		if(JavaUtil.getInstance().isDouble(name))
			return Optional.of(edge+Double.valueOf(name));
		return Optional.empty();
	}
	
	/**
	 * Gets a color from the given argument
	 */
	public ArgsReturn<Color> nextColor(){
		return this.parser(JavaUtil.getInstance()::getColorFromString);
	}
	
	/**
	 * Gets an integer
	 */
	public ArgsReturn<Integer> nextInteger(){
		return this.parser(i-> JavaUtil.getInstance().isInt(i)?Optional.of(Integer.valueOf(i)):Optional.empty());
	}

	/**
	 * Gets an item
	 */
	public ArgsReturn<Item> nextItem(){
		return this.parser(ItemUtil.getInstance()::getItemFromName);
	}
	
	/**
	 * Gets an compound-nbt
	 */
	public ArgsReturn<CompoundNBT> nextNbt(){
		//Gets the left strings
		ArgsReturn<String> left = this.nextStringleft();

		//Checks if the nbt was given
		if(left.hasArgumentError())
			return new ArgsReturn<>();

		//Parses the nbt
		Optional<CompoundNBT> optNbt = ItemUtil.getInstance().stringToNbt(left.get());

		//Checks if the nbt could be parsed
		if(optNbt.isPresent())
			return new ArgsReturn<CompoundNBT>(ItemUtil.getInstance().stringToNbt(left.get()).get(),left.getAsString());

		return new ArgsReturn<>(left.getAsString());
	}
	
	/**
	 * Gets a keycode by the static name from the GLFW-library
	 * */
	public ArgsReturn<Integer> nextKeyCode(){
		return this.parser(i->{
			//Gets the field
			Optional<Field> optField = ReflectionUtil.getInstance().get(GLFW.class,"GLFW_KEY_"+i.toUpperCase());

			//Checks if the field exists
			if(!optField.isPresent())
				return Optional.empty();

			//Gets the value
			return ReflectionUtil.getInstance().get(optField.get(),null);
		});
	}
	
	/**
	 * Gets a module
	 */
	public ArgsReturn<Module> nextModule(){
		return this.parser(Toolsmod.getInstance().getModuleManager()::getModuleByName);
	}

	/**
	 * Interface as lambdas to easier parse some values
	 * */
	@FunctionalInterface
	static interface IParser<T>{
		public Optional<T> exec(String get);
	}
}

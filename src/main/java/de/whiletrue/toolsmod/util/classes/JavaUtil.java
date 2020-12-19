package de.whiletrue.toolsmod.util.classes;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

public class JavaUtil {

	private static JavaUtil instance;
	
	private JavaUtil() {}
	
	public static JavaUtil getInstance() {
		if(instance==null)
			instance=new JavaUtil();
		return instance;
	}

	/**
	 * @param value the current enum value
	 * @return the technically previously defined value of the current enum type
	 */
	public <T extends Enum<?>> T getEnumPre(T value) {
		//Gets all values
		@SuppressWarnings({ "unchecked"})
		T[] presets = (T[]) value.getDeclaringClass().getEnumConstants();
		
		//Iterates over all values
		for(int i =0;i<presets.length;i++)
			//Checks if the value matches
			if(presets[i]==value)
				//Returns the previous value
				return i-1<0?presets[presets.length-1]:presets[i-1];
		
		//False positive case (Should not occur)
		return value;
	}
	
	/**
	 * @param value the current enum value
	 * @return the technically next defined value of the current enum type
	 */
	public <T extends Enum<?>> T getEnumNext(T value) {
		//Gets all values
		@SuppressWarnings({ "unchecked"})
		T[] presets = (T[]) value.getDeclaringClass().getEnumConstants();
		
		//Iterates over all values
		for(int i =0;i<presets.length;i++)
			//Checks if the value matches
			if(presets[i]==value)
				//Returns the previous value
				return i+1==presets.length?presets[0]:presets[i+1];

		//False positive case (Should not occur)
		return value;
	}
	
	/**
	 * Returns if the given string can be converted into an double
	 */
	public boolean isDouble(String s) {
		try {
			Double.valueOf(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	
	/**
	 * Returns if the given string can be converted into an int
	 */
	public boolean isInt(String s) {
		try {
			Integer.valueOf(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * Returns if the given string can be converted into an float
	 */
	public boolean isFloat(String s) {
		try {
			Float.valueOf(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Tries to get a color from a string
	 * @param value the value
	 * @return optionally the color
	 */
	public Optional<Color> getColorFromString(String value){
		//Try's to get the color from the default java.awt colors
		Optional<Field> optField = ReflectionUtil.getInstance().get(Color.class, value);

		//Checks if the color got found
		if(optField.isPresent())
			//Returns the colors value
			return ReflectionUtil.getInstance().get(optField.get(), null);
		
		//Tries to decode a hex color or rgb color
		try {
			return Optional.of(Color.decode(value));
		} catch (Exception e) {}
		
		//Failed to get color
		return Optional.empty();
	}
	
	/**
	 * Converts an input stream into a string
	 * @param in the inputstream
	 */
	public String convertInputStreamToString(InputStream in) {
		return new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)).lines()
				.collect(Collectors.joining("\n"));
	}
}

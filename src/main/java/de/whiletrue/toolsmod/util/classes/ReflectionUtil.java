package de.whiletrue.toolsmod.util.classes;

import java.lang.reflect.Field;
import java.util.Optional;

public class ReflectionUtil {

	private static ReflectionUtil instance;
	
	private ReflectionUtil() {}
	
	public static ReflectionUtil getInstance() {
		if(instance==null)
			instance=new ReflectionUtil();
		return instance;
	}
	
	/**
	 * Returns the field from a class by its name.
	 *
	 * @param clazz the class from where to get the field
	 * @param name the field's name
	 */
	public Optional<Field> get(Class<?> clazz,String name) {
		try {
			return Optional.of(clazz.getDeclaredField(name));
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	/**
	 * Returns the field from a class by its name.
	 * If an error occurs, it crashes the game
	 *
	 * @param clazz the class from where to get the field
	 * @param name the field's name
	 * */
	public Field getOrDie(Class<?> clazz,String name) {
		try {
			return clazz.getDeclaredField(name);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return null;
	}
	
	/**
	 * Try's to set a value to a given field
	 *
	 * @param instance the instance on where to set the value
	 * @param field the field's name
	 * @param value the value that should be set
	 *
	 * @return if the new field could be set
	 */
	public boolean set(Object instance,Field field,Object value) {
		try {
			//Sets the field accessible
			field.setAccessible(true);
			//Sets the new value
			field.set(instance,value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Returns the value of a object from a field
	 *
	 * @param field the field from where to get the value
	 * @param instance the instance of the fields class
	 */
	@SuppressWarnings("unchecked")
	public<T> Optional<T> get(Field field,Object instance){
		try {
			field.setAccessible(true);
			return Optional.of((T) field.get(instance));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}

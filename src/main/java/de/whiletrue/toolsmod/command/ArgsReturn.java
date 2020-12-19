package de.whiletrue.toolsmod.command;

/*
 * Returns the requested value or an error
 * */
public class ArgsReturn<T>{

	private T value;
	private String asString;
	private EdgeCodes code;

	/**
	 * Gives the parsed value and the raw string
	 *
	 * @param value the value
	 * @param asString the raw string
	 * */
	public ArgsReturn(T value,String asString) {
		this.value=value;
		this.asString = asString;
	}

	/**
	 * Gives the parse-error
	 *
	 * @param asString the raw string that got parsed
	 * */
	public ArgsReturn(String asString) {
		this.asString=asString;
		this.code=EdgeCodes.PARSE_ERROR;
	}

	/**
	 * Gives the argument-present error
	 * */
	public ArgsReturn() {
		this.code=EdgeCodes.ARGUMENT_NOT_PRESENT;
	}

	/**
	 * Gets the value in case nothing wrong happen
	 * */
	public T get() {
		return this.value;
	}

	/**
	 * Gets thr raw string
	 * */
	public String getAsString() {
		return this.asString;
	}

	/**
	 * Checks if no error occurred
	 * */
	public boolean isPresent() {
		return !this.isEmpty();
	}

	/**
	 * Checks if an error occurred
	 * */
	public boolean isEmpty() {
		return this.value==null;
	}

	/**
	 * Checks if an error occurred and if so if
	 * the error is the same as the given one
	 *
	 * @param code the code to test
	 * */
	public boolean isCode(EdgeCodes code) {
		return this.code!=null&&this.code.equals(code);
	}

	/**
	 * Returns if the argument has a parse error
	 * */
	public boolean hasParseError(){
		return this.isCode(EdgeCodes.PARSE_ERROR);
	}

	/**
	 * Returns if the argument was not given
	 * */
	public boolean hasArgumentError(){
		return this.isCode(EdgeCodes.ARGUMENT_NOT_PRESENT);
	}

	/**
	 * Enum with all error codes
	 * */
	public enum EdgeCodes{
		ARGUMENT_NOT_PRESENT,
		PARSE_ERROR
	}
}
package de.whiletrue.toolsmod.util;

public class Timer {

	//Time reference
	private long time;
	
	public Timer() {
		this.reset();
	}
	
	/*
	 * Resets the time
	 */
	public void reset() {
		this.time=System.currentTimeMillis();
	}
	
	/**
	 * @param time the time to check for
	 * @return if the timer has been running longer than the given time
	 */
	public boolean hasReached(long time) {
		return this.getRunningTime() > time;
	}
	
	/**
	 * @return the amount of milliseconds the timer has been running
	 */
	public long getRunningTime() {
		return System.currentTimeMillis()-this.time;
	}
}

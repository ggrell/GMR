package grovepi.common;

/** @author Dan Jackson, Newcastle University, 2015. */
public class Delay {
	
	public static void milliseconds(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
	
}

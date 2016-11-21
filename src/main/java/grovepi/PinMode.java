package grovepi;

/** @author Dan Jackson, Newcastle University, 2015. */

/*
public class PinMode
{
	public static final byte INPUT = 0;
	public static final byte OUTPUT = 1;
}
*/

public enum PinMode
{
	INPUT(0),
	OUTPUT(1),
	;
	
	final int value;
	
	PinMode(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}

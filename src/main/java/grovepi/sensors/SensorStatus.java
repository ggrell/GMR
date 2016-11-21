package grovepi.sensors;

/** @author Dan Jackson, Newcastle University, 2015. */

/*
public enum SensorStatus
{
	OFF(0),
    ON(1);
	
	final int value;
	
	SensorStatus(int value) {
		this.value = value;
	}
	
	int getValue() {
		return value;
	}
	
	public static SensorStatus fromValue(int value) {
		if (value == 0) { return OFF; }
		return ON;
	}
}
*/

public class SensorStatus
{
	public static final boolean OFF = false;
	public static final boolean ON = true;
}

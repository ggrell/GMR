package grovepi.sensors;

import grovepi.Constants;
import grovepi.GrovePi;

/** @author Dan Jackson, Newcastle University, 2015. Originally based on code by Johannes Bergmann, and C# code by Jonathan Robson. */
public class UltrasonicRangerSensor {

    private static final int COMMAND_ADDRESS = 7;

    protected final GrovePi device;
    protected final int pin;
	
	public UltrasonicRangerSensor(GrovePi device, int pin) {
		this.device = device;
		this.pin = pin;
	}
	
    public int getDistance() {
        byte[] buffer = new byte[] {COMMAND_ADDRESS, (byte)pin, Constants.UNUSED, Constants.UNUSED};
        device.getDirectAccess().write(buffer);
grovepi.common.Delay.milliseconds(200);	// C# version doesn't do this
        device.getDirectAccess().read(buffer);
        return ((buffer[1] & 0xFF) << 8) + (buffer[2] & 0xFF);
    }	
	
}

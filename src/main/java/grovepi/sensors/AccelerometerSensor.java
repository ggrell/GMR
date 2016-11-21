
package grovepi.sensors;

import grovepi.Constants;
import grovepi.GrovePi;

/** @author Dan Jackson, Newcastle University, 2015. Derived from C# code by Jonathan Robson. */
public class AccelerometerSensor {
	
    private static final int COMMAND_ADDRESS = 20;

    protected final GrovePi device;
    protected final int pin;
	
	public AccelerometerSensor(GrovePi device, int pin) {
		this.device = device;
		this.pin = pin;
	}

	public byte[] read() {
        byte[] buffer = new byte[] { (byte)COMMAND_ADDRESS, (byte)pin, Constants.UNUSED, Constants.UNUSED};
        device.getDirectAccess().write(buffer);

// TODO: C# version looks wrong here, need to look into this and fix.
        byte[] readBuffer = new byte[4];
        device.getDirectAccess().read(readBuffer);
        
        if (Byte.toUnsignedInt(readBuffer[1]) > 32)
            readBuffer[1] = (byte) -(Byte.toUnsignedInt(readBuffer[1]) - 224);
        if (Byte.toUnsignedInt(readBuffer[2]) > 32)
            readBuffer[2] = (byte) -(Byte.toUnsignedInt(readBuffer[2]) - 224);
        if (Byte.toUnsignedInt(readBuffer[3]) > 32)
            readBuffer[3] = (byte) -(Byte.toUnsignedInt(readBuffer[3]) - 224);

        return readBuffer;
    }
	
}

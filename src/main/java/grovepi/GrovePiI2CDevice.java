package grovepi;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

/**
 * Wraps I2CDevice with read/write that mirrors Windows 10 IoT Windows.Devices.I2c.I2cDevice.
 * 
 * Retries I2C writes, and RuntimeException instead of checked IOException.
 * 
 * @author Dan Jackson, Newcastle University, 2015.
 */
public class GrovePiI2CDevice
{
	public final static String BUS = "I2C1"; //"fake"; //"I2C1";
	public final static int ADDRESS = 4;
    
	private final I2CDevice device;
    I2CDevice getI2CDevice() { return device; }    
	
	public GrovePiI2CDevice(I2CDevice device) {
		this.device = device;
	}    
	
	// Map a Windows 10 IoT "I2C name" for the I2cDevice.GetDeviceSelector()
	public static I2CBus getI2CBusFromString(String s) throws IOException {
		if (s.equals("I2C0")) { return I2CFactory.getInstance(I2CBus.BUS_0); } 
		else if (s.equals("I2C1")) { return I2CFactory.getInstance(I2CBus.BUS_1); }
		throw new IOException("Unknown I2C bus name");
	}
	
	
	public static GrovePiI2CDevice createInstance() throws IOException {
		return createInstance(BUS, ADDRESS);
	}

	public static GrovePiI2CDevice createInstance(String bus, int address) throws IOException {
		I2CDevice device;
		if (bus.equalsIgnoreCase("fake")) {
			device = new FakeI2CDevice(address);
		} else {
			device = getI2CBusFromString(bus).getDevice(address);
		}
		return new GrovePiI2CDevice(device);
	}

	
	public static GrovePiI2CDevice createInstanceRuntimeExecption() {
		try {
			return createInstance();
		} catch (IOException e) {
		   throw new RuntimeException(e);
		}
	}
	
	
	public static GrovePiI2CDevice createInstanceTry() {
		try {
			return createInstance();
		} catch (IOException e) {
		   return null;
		}
	}
	
	public int read() {
		final int maxRetries = 8;
		for (int retries = 0; ; retries++) {
			try {
				return device.read();
			} catch (IOException e) {
				//System.err.print("<I2C-Read-Error-" + (retries + 1) + ">");
				if (retries >= maxRetries) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	
	public void read(byte[] buffer) {
		final int maxRetries = 8;
		for (int retries = 0; ; retries++) {
			try {
				device.read(1, buffer, 0, buffer.length);
				return;
			} catch (IOException e) {
				//System.err.print("<I2C-Read-Error-" + (retries + 1) + ">");
				if (retries >= maxRetries) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	
	public void write(byte[] buffer) {
		final int maxRetries = 8;
		for (int retries = 0; ; retries++) {
			try {
				device.write(1, buffer, 0, buffer.length);
				return;
			} catch (IOException e) {
				//System.err.print("<I2C-Error-" + (retries + 1) + ">");
				if (retries >= maxRetries) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	
/*	
	// Convenience function (allocates buffer)
	public byte[] read(int numberOfBytes) {
		byte[] buffer = new byte[numberOfBytes];
		read(buffer);
		return buffer;
	}

	// Convenience function (accepts variable number of parameters instead of an array)
	public void write(int... bytes) {
		byte[] buffer = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			buffer[i] = (byte)bytes[i];
		}
		write(buffer);
	}
*/
	
}
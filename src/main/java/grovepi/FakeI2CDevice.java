package grovepi;

import com.pi4j.io.i2c.I2CDevice;

public class FakeI2CDevice implements I2CDevice {

	static final int NUM_PINS = 20;
	int[] pinValue = new int[NUM_PINS];
	int[] pinMode = new int[NUM_PINS];
	
	int address;
	private byte[] response = null;
		
	FakeI2CDevice(int address) {
		this.address = address;
	}
	
	// This method writes several bytes directly to the i2c device from given buffer at given offset.
	public void write(byte[] buffer, int offset, int size) {
		if (buffer == null || size <= 0) { return; }
		this.response = null;
		if (size >= 4) {
			switch (buffer[offset + 0]) {
				case GrovePi.Command.DIGITAL_READ: // 1
				{
					int pin = (int)buffer[1];
					int value = 0;
					if (pin >= 0 && pin < NUM_PINS) {
						value = pinValue[pin];
					}
					response = new byte[1];
					response[0] = (byte)value;
					break;
				}
				case GrovePi.Command.DIGITAL_WRITE: // 2
				{
					int pin = (int)buffer[1];
					if (pin >= 0 && pin < NUM_PINS) {
						pinValue[pin] = Byte.toUnsignedInt(buffer[2]);
					}
					break;
				}
				case GrovePi.Command.ANALOG_READ: // 3
				{
					int pin = (int)buffer[1];
					int value = 0;
					if (pin >= 0 && pin < NUM_PINS) {
						value = pinValue[pin];
					}
					response = new byte[1];
					response[0] = (byte)value;
					break;
				}
				case GrovePi.Command.ANALOG_WRITE: // 4
				{
					int pin = (int)buffer[1];
					if (pin >= 0 && pin < NUM_PINS) {
						pinValue[pin] = Byte.toUnsignedInt(buffer[2]);
					}
					break;
				}
				case GrovePi.Command.PIN_MODE: // 5
				{
					int pin = (int)buffer[1];
					if (pin >= 0 && pin < NUM_PINS) {
						pinMode[pin] = Byte.toUnsignedInt(buffer[2]);
					}
					break;
				}
				case GrovePi.Command.VERSION: // 8
				{
			    	this.response = new byte[] { GrovePi.Command.VERSION, (byte)'f', (byte)'a', (byte)'k' };
					break;
				}
			}
		}		
	}

	// This method writes one byte directly to i2c device.
	public void write(byte b) {
		byte[] buffer = new byte[] { b };
		write(buffer, 0, buffer.length);
	}

	// This method writes one byte to i2c device.
	public void write(int address, byte b) {
		byte[] buffer = new byte[] { (byte) address, b };
		write(buffer, 0, buffer.length);
	}

	// This method writes several bytes to the i2c device from given buffer at
	// given offset.
	public void write(int address, byte[] buffer, int offset, int size) {
		byte[] tempBuffer = new byte[1 + size];
		tempBuffer[0] = (byte) address;
		System.arraycopy(buffer, offset, tempBuffer, 1, size);
		write(tempBuffer, 0, tempBuffer.length);
	}

	// This method reads bytes directly from the i2c device to given buffer at asked offset.
	public int read(byte[] buffer, int offset, int size) {
		write(buffer, offset, size);
		if (this.response == null || this.response.length <= 0) { return -1; }
		if (buffer != null && size > 0) {
			System.arraycopy(this.response, 0, buffer, offset, Math.min(size,  this.response.length));
		}
		return this.response[this.response.length - 1];
	}

	// This method reads one byte from the i2c device.
	public int read() {
		return read(null, 0, 0);
	}

	// This method reads bytes from the i2c device to given buffer at asked offset.
	public int read(int address, byte[] buffer, int offset, int size) {
		byte[] tempBuffer = new byte[1 + size];
		tempBuffer[0] = (byte) address;
		System.arraycopy(buffer, offset, tempBuffer, 1, size);
		return read(tempBuffer, 0, tempBuffer.length);
	}

	// This method reads one byte from the i2c device.
	public int read(int address) {
		byte[] buffer = new byte[] { 0 };
		return read(address, buffer, 0, buffer.length);
	}

	// This method writes and reads bytes to/from the i2c device in a single method call
	public int read(byte[] writeBuffer, int writeOffset, int writeSize, byte[] readBuffer, int readOffset, int readSize) {
		write(writeBuffer, writeOffset, writeSize);
		return read (readBuffer, readOffset, readSize);
	}

}

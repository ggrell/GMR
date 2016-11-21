package grovepi.sensors;

import grovepi.GrovePi;
import grovepi.PinMode;

/**
 * @author Dan Jackson, Newcastle University, 2015.
 */
public abstract class Sensor {
	protected final GrovePi device;
	protected final int pin;

	protected Sensor(GrovePi device, int pin, PinMode pinMode) {
		device.pinMode(pin, pinMode);
		this.device = device;
		this.pin = pin;
	}

	public boolean getState() {
		return device.digitalRead(pin) == 0 ? SensorStatus.OFF : SensorStatus.ON;
	}

	public void setState(float newState) {
		int value = (int) (255 * newState + 0.5);
		device.analogWrite(pin, value);
	}

	public void setState(int newState) {
		device.digitalWrite(pin, (byte) newState);
	}

	public void setState(boolean newState) {
		setState(!newState ? 0 : 1);
	}

}

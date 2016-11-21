package grovepi.sensors;

import grovepi.GrovePi;
import grovepi.PinMode;

/** @author Dan Jackson, Newcastle University, 2015. */
public class Relay extends Sensor {

	public Relay(GrovePi device, int pin) {
		super(device, pin, PinMode.OUTPUT);
	}

}

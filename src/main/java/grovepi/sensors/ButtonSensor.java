package grovepi.sensors;

import grovepi.GrovePi;
import grovepi.Pin;
import grovepi.PinMode;

/** @author Dan Jackson, Newcastle University, 2015. */
public class ButtonSensor extends Sensor {

	public ButtonSensor(GrovePi device, int pin) {
		super(device, pin, PinMode.INPUT);
	}

	// [dgj] Added
	public boolean isPressed() {
		return getState();
	}
	
	
	
	public static void main(String[] args) {
		GrovePi grovePi = new GrovePi();
		ButtonSensor button = grovePi.getDeviceFactory().createButtonSensor(Pin.DIGITAL_PIN_4);
		for(;;) {
            System.out.print(button.isPressed() ? 1 : 0);
		}
	}
	
}

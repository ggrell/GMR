package grovepi.sensors;

import grovepi.Constants;
import grovepi.GrovePi;
import grovepi.PinMode;

/** @author Dan Jackson, Newcastle University, 2015. Derived from C# code by Jonathan Robson. */
public class SoundSensor
{
    protected final GrovePi device;
    protected final int pin;

    public SoundSensor(GrovePi device, int pin) {
        this.device = device;
        this.pin = pin;
        device.pinMode(pin, PinMode.INPUT);
    }

    public int getValue() {
    	return device.analogRead(pin);
    }

    public double getVoltage() {
        int sensorValue = getValue();
        return Math.round(((float)sensorValue * Constants.ADC_VOLTAGE / 1023 * 100)) / 100.0f;
    }
   

}

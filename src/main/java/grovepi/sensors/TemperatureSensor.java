package grovepi.sensors;

import grovepi.GrovePi;
import grovepi.PinMode;

/** @author Dan Jackson, Newcastle University, 2015. Based on the C# code by Jonathan Robson. */
public class TemperatureSensor
{
	public static class Model {
		public static final int ONE_POINT_ZERO = 3975;
		public static final int ONE_POINT_ONE = 4250;
		public static final int ONE_POINT_TWO = 4250;
	}
	
    protected final GrovePi device;
    protected final int pin;
    protected final int model;

    public TemperatureSensor(GrovePi device, int pin, int model) {
        this.device = device;
        this.pin = pin;
        this.model = model;
        device.pinMode(pin, PinMode.INPUT);
    }

    public double getTemperatureInCelcius() {
    	double result = (double)device.analogRead(pin);
    	if (result == 0) { result = 1; }
    	double resistance = (1023 - result) * 10000 / result;
    	return 1/(Math.log(resistance/10000) / (int)model + 1/298.15) - 273.15;
    }

}

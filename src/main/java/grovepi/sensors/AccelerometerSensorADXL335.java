package grovepi.sensors;

import grovepi.Constants;
import grovepi.GrovePi;
import grovepi.Pin;
import grovepi.PinMode;

/** @author Dan Jackson, Newcastle University, 2015. Derived from C code by FrankieChu */
public class AccelerometerSensorADXL335 {

	static final int X_AXIS_PIN = Pin.ANALOG_PIN_0;
	static final int Y_AXIS_PIN = Pin.ANALOG_PIN_1;
	static final int Z_AXIS_PIN = Pin.ANALOG_PIN_2;

	static final float ZERO_X = 1.22f; 		// Acceleration of X-AXIS is 0g, the voltage of X-AXIS is 1.22v
	static final float ZERO_Y = 1.22f; 		// Acceleration of X-AXIS is 0g, the voltage of X-AXIS is 1.22v
	static final float ZERO_Z = 1.25f; 		// Acceleration of X-AXIS is 0g, the voltage of X-AXIS is 1.25v
	static final float SENSITIVITY = 0.25f; // Sensitivity of X/Y/Z axis is 0.25v/g

    protected final GrovePi device;
    protected float scale;
	
	public AccelerometerSensorADXL335(GrovePi device) {
		this.device = device;
		
		device.pinMode(X_AXIS_PIN, PinMode.INPUT);
		device.pinMode(Y_AXIS_PIN, PinMode.INPUT);
		device.pinMode(Z_AXIS_PIN, PinMode.INPUT);
		
		this.scale = (float)SENSITIVITY * Constants.ADC_AMPLITUDE / Constants.ADC_VOLTAGE;
	}

	
	public int[] getXYZ() {
		int[] values = new int[3];
		values[0] = device.analogRead(X_AXIS_PIN);
		values[1] = device.analogRead(Y_AXIS_PIN);
		values[2] = device.analogRead(Z_AXIS_PIN);
		return values;
	}
	
	public float[] getAcceleration() {
		int[] intValues = getXYZ();
		float[] values = new float[3];
		values[0] = intValues[0];
		
		int[] xyz = getXYZ();
		float xvoltage = (float)xyz[0] * Constants.ADC_VOLTAGE / Constants.ADC_AMPLITUDE;
		float yvoltage = (float)xyz[1] * Constants.ADC_VOLTAGE / Constants.ADC_AMPLITUDE;
		float zvoltage = (float)xyz[2] * Constants.ADC_VOLTAGE / Constants.ADC_AMPLITUDE;
		values[0] = (xvoltage - ZERO_X)/SENSITIVITY;
		values[1] = (yvoltage - ZERO_Y)/SENSITIVITY;
		values[2] = (zvoltage - ZERO_Z)/SENSITIVITY;
		
		return values;
	}

	
}

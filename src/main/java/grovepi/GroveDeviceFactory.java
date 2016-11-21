package grovepi;

import grovepi.i2c_devices.RgbLcdDisplay;
import grovepi.sensors.AccelerometerSensor;
import grovepi.sensors.AccelerometerSensorADXL335;
import grovepi.sensors.ButtonSensor;
import grovepi.sensors.Buzzer;
import grovepi.sensors.Led;
import grovepi.sensors.LightSensor;
import grovepi.sensors.Relay;
import grovepi.sensors.RotaryAngleSensor;
import grovepi.sensors.SoundSensor;
import grovepi.sensors.TemperatureAndHumiditySensor;
import grovepi.sensors.UltrasonicRangerSensor;

/** @author Dan Jackson, Newcastle University, 2015. */
public class GroveDeviceFactory {

	protected final GrovePi device;
	
	public GroveDeviceFactory(GrovePi device) {
		this.device = device;
	}
	
    public Relay createRelay(int pin) {
    	return new Relay(device, pin);
    }
	
    public Led createLed(int pin) {
    	return new Led(device, pin);
    }

    public TemperatureAndHumiditySensor createTemperatureAndHumiditySensor(int pin) {
        return new TemperatureAndHumiditySensor(device, pin);
    }

    public TemperatureAndHumiditySensor createTemperatureAndHumiditySensor(int pin, int model) {
        return new TemperatureAndHumiditySensor(device, pin, model);
    }

    public UltrasonicRangerSensor createUltraSonicSensor(int pin) {
    	return new UltrasonicRangerSensor(device, pin);
    }
    
    public AccelerometerSensor createAccelerometerSensor(int pin) {
    	return new AccelerometerSensor(device, pin);
	}
    
    public AccelerometerSensorADXL335 createAccelerometerSensorADXL335(int pin) {
    	return new AccelerometerSensorADXL335(device);
    }
    
    //public RealTimeClock createRealTimeClock(int pin);
    //public LedBar createBuildLedBar(int pin);
    //public FourDigitDisplay createFourDigitDisplay(int pin);
    //public ChainableRgbLed createChainableRgbLed(int pin);
    
    public RotaryAngleSensor createRotaryAngleSensor(int pin) {
    	return new RotaryAngleSensor(device, pin);
    }
    
    public Buzzer createBuzzer(int pin) {
    	return new Buzzer(device, pin); 
    }
    
    public SoundSensor createSoundSensor(int pin) {
    	return new SoundSensor(device, pin);
    }
    
    public LightSensor createLightSensor(int pin) {
    	return new LightSensor(device, pin);
    }
    
    public ButtonSensor createButtonSensor(int pin) {
    	return new ButtonSensor(device, pin);
    }
    
    public RgbLcdDisplay createRgbLcdDisplay() {
    	return new RgbLcdDisplay();
	}
    
    public RgbLcdDisplay createRgbLcdDisplay(int rgbAddress, int textAddress) {
    	return new RgbLcdDisplay(rgbAddress, textAddress);
    }
	
}


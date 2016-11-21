package grovepi;

/** @author Dan Jackson, Newcastle University, 2015. */
public class Pin
{
	public static final int ANALOG_PIN_0 = 0;	// (same as D14?) 
	public static final int ANALOG_PIN_1 = 1;	// (same as D15?) 
	public static final int ANALOG_PIN_2 = 2;	// analogRead(2) (digitalRead(2) reads D2) (same as D16?) 
	//public static final int ANALOG_PIN_3 = 0;	// Actually socket A2's second pin (same as D17?) 
	//public static final int ANALOG_PIN_4 = 0;	// A4 used for I2C/SDA (same as D18?) 
	//public static final int ANALOG_PIN_5 = 0;	// A5 used for I2C/SCL (same as D19?) 
	 
	//public static final int DIGITAL_PIN_0 = 1;	// D0 is Serial RX
	//public static final int DIGITAL_PIN_1 = 1;	// D1 is Serial TX
	public static final int DIGITAL_PIN_2 = 2;	// digitalRead(2) (analogRead(2) reads A2)
	public static final int DIGITAL_PIN_3 = 3;	// D3 Supports PWM ("analogWrite")
	public static final int DIGITAL_PIN_4 = 4;
	public static final int DIGITAL_PIN_5 = 5;	// D5 Supports PWM ("analogWrite")
	public static final int DIGITAL_PIN_6 = 6;	// D6 Supports PWM ("analogWrite")
	public static final int DIGITAL_PIN_7 = 7;
	public static final int DIGITAL_PIN_8 = 8;
}

/*
public enum Pin
{
	ANALOG_PIN_0(0),
	DIGITAL_PIN_2(2),
	DIGITAL_PIN_3(3),
	DIGITAL_PIN_4(4),
	// [dgj] Why aren't more pins defined?
	;
	
	final int value;
	
	Pin(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}
*/

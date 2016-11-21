package grovepi.i2c_devices;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

/**
 * @author Johannes Bergmann
 */
public class MotorDriver {

	private static final int STEP_SLEEP = 100;

	private static final int MOTOR_DRIVER_ADDR = 0x0f;

	private static final byte MotorSpeedSet = (byte) 0x82;
	//private static final byte PWMFrequenceSet = (byte) 0x84;
	private static final byte DirectionSet = (byte) 0xaa;
	//private static final byte MotorSetA = (byte) 0xa1;
	//private static final byte MotorSetB = (byte) 0xa5;
	private static final byte Nothing = (byte) 0x01;
	//private static final byte EnableStepper = (byte) 0x1a;
	//private static final byte UnenableStepper = (byte) 0x1b;
	//private static final byte Stepernu = (byte) 0x1c;

	public static final byte DIRECTION_FWD_BOTH = 0x0a;	//0b1010;
	public static final byte DIRECTION_FWD_BWD = 0x09;	//0b1001;
	public static final byte DIRECTION_BWD_BOTH = 0x05;	//0b0101;
	public static final byte DIRECTION_BWD_FWD = 0x06;	//0b0110;

	public static void main(String[] args) throws IOException {
		MotorDriver motorDriver = new MotorDriver();

		int speedMin = 40;
		int speedMax = 255;
		int rotateMs = 1000;
		int driveMs = 600;

		motorDriver.setDirection(DIRECTION_BWD_BOTH);
		motorDriver.setSpeed(speedMax, speedMax);
		motorDriver.sleep(rotateMs);

		motorDriver.setDirection(DIRECTION_FWD_BOTH);
		motorDriver.setSpeed(speedMax, speedMax);
		motorDriver.sleep(rotateMs);

		motorDriver.setDirection(DIRECTION_BWD_FWD);
		motorDriver.setSpeed(speedMin, speedMin);
		motorDriver.adaptSpeed(speedMax, driveMs);
		motorDriver.adaptSpeed(speedMin, driveMs);

		motorDriver.setDirection(DIRECTION_FWD_BWD);
		motorDriver.setSpeed(speedMin, speedMin);
		motorDriver.adaptSpeed(speedMax, driveMs);
		motorDriver.adaptSpeed(speedMin, driveMs);

		motorDriver.setSpeed(0, 0);
	}

	private final I2CDevice motorDevice;
	private int speedA = 0;
	private int speedB = 0;

	public MotorDriver() throws IOException {
		final I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
		motorDevice = bus.getDevice(MOTOR_DRIVER_ADDR);
		System.out.println("Waiting for Motor Driver initialization...");
		sleep(1000);
		System.out.println("...done");
	}

	public void sleep(int msec) {
		if (msec > 10) {
			System.out.println("Sleep " + msec);
		}
		try {
			Thread.sleep(msec);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Adapts the speed to reach the given target speed.
	 * 
	 * @param targetSpeed
	 *            Speed to be reached (0-255)
	 * @param msec
	 *            Milliseconds until target speed will be reached.
	 * @throws IOException
	 */
	public void adaptSpeed(int targetSpeed, int msec) throws IOException {
		if (msec < STEP_SLEEP) {
			msec = STEP_SLEEP;
		}
		int steps = msec / 100;
		int stepA = (targetSpeed - speedA) / steps;
		int stepB = (targetSpeed - speedB) / steps;
		for (int step = 0; step < steps; step++) {
			setSpeed(speedA + stepA, speedB + stepB);
			sleep(STEP_SLEEP);
		}
		setSpeed(targetSpeed, targetSpeed);
	}

	public void setSpeed(int speedA, int speedB) throws IOException {
		System.out.println("SpeedA: " + speedA + " SpeedB: " + speedB);
		this.speedA = speedA;
		this.speedB = speedB;
		motorDevice.write(MotorSpeedSet, new byte[] { (byte) speedA,
				(byte) speedB }, 0, 2);
		sleep(10);
	}

	public void setDirection(int direction) throws IOException {
		System.out.println("Direction: " + direction);
		motorDevice.write(DirectionSet,
				new byte[] { (byte) direction, Nothing }, 0, 2);
		sleep(10);
	}
}

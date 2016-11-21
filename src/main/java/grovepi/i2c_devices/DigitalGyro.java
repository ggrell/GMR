package grovepi.i2c_devices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

/**
 * Controls the Grove 3-Axis Digital Gyro
 * (http://www.seeedstudio.com/wiki/Grove_-_3-Axis_Digital_Gyro).
 * 
 * Data sheet of ITG-3200 chip:
 * http://garden.seeedstudio.com/images/a/a9/ITG-3200.pdf
 * 
 * Data sheet of manufacturer:
 * https://www.sparkfun.com/datasheets/Sensors/Gyro/PS-ITG-3200-00-01.4.pdf
 * 
 * @author Johannes Bergmann
 */
// TODO: Implement retry or change I2C rate so that fewer errors occur?
public class DigitalGyro {

	private static DigitalGyro digitalGyro;

	public static void main(String[] args) throws IOException {
		int errors = 0;
		digitalGyro = new DigitalGyro();
		System.out.println("I2C Id: " + digitalGyro.whoAmI());
		List<Double> temp = new ArrayList<>();
		List<Integer> x = new ArrayList<>();
		List<Integer> y = new ArrayList<>();
		List<Integer> z = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			try {
				double t = digitalGyro.getTemperature();
				temp.add(Double.valueOf(t / 100));
				int[] xyz = digitalGyro.getVelocity();
				x.add(Integer.valueOf(xyz[0]));
				y.add(Integer.valueOf(xyz[1]));
				z.add(Integer.valueOf(xyz[2]));
				digitalGyro.sleep(100);
			} catch (IOException e) {
				errors++;
			}
		}
		System.out.println("Errors: " + errors);
		System.out.println(temp);
		System.out.println(x);
		System.out.println(y);
		System.out.println(z);
		System.out.println("Avarages: ");
		System.out.println(avarage(x));
		System.out.println(avarage(y));
		System.out.println(avarage(z));
	}

	private static int avarage(List<Integer> list) {
		long sum = 0;
		for (Integer value : list) {
			sum += value.intValue();
		}
		return Math.round(sum / list.size());
	}

	private static final byte GYRO_ADDRESS = 0x68;
	// ITG3200 addresses
	private static final byte WHO_AM_I = (byte) 0x00;
	private static final byte GYRO_SAMPLE_RATE_DEVIDER = (byte) 0x15;
	private static final byte GYRO_DLPF = (byte) 0x16;
	//private static final byte ITG3200_byte_C = (byte) 0x17;
	//private static final byte ITG3200_byte_S = (byte) 0x1A;
	private static final byte TEMP_OUT_H = (byte) 0x1B;
	private static final byte TEMP_OUT_L = (byte) 0x1C;
	private static final byte ITG3200_GX_H = (byte) 0x1D;
	private static final byte ITG3200_GX_L = (byte) 0x1E;
	private static final byte ITG3200_GY_H = (byte) 0x1F;
	private static final byte ITG3200_GY_L = (byte) 0x20;
	private static final byte ITG3200_GZ_H = (byte) 0x21;
	private static final byte ITG3200_GZ_L = (byte) 0x22;
	//private static final byte ITG3200_PWR_M = (byte) 0x3E;

	private final I2CDevice device;

	private final int offsetX = 0;
	private final int offsetY = 0;;
	private final int offsetZ = 0;
	private final I2CBus bus;;

	public DigitalGyro() throws IOException {
		bus = I2CFactory.getInstance(I2CBus.BUS_1);
		device = bus.getDevice(GYRO_ADDRESS);
		sleep(100);
		initialize();
	}

	public void sleep(int msec) {
		try {
			Thread.sleep(msec);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	public void initialize() throws IOException {
		// Send a reset
		// TODO Try setting CLL_SEL to one of the gyros as recommended in sheet
		// see page 27
		// device.write(ITG3200_PWR_M, (byte) 0x80);
		sleep(100);

		// Fsample = Finternal / (divider+1)
		// where F internal is either 1kHz or 8kHz
		// As an example, if the internal sampling is at 1kHz, then setting this
		// register to 7 would give the following:
		// Fsample = 1kHz / (7 + 1) = 125Hz
		// Set sample rate to 100 hz
		device.write(GYRO_SAMPLE_RATE_DEVIDER, (byte) 9);

		sleep(100);
		setConfiguration();
	}

	private void setConfiguration() throws IOException {
		// DPLF_CFG: Low pass filter, Sample rate
		// 0: 256Hz, 8kHz
		// 1: 188Hz, 1kHz
		// 2: 98Hz, 1kHz
		// 3: 42Hz, 1kHz
		// 4: 20Hz, 1kHz
		// 5: 10Hz, 1kHz
		// 6: 5Hz, 1kHz
		byte dlpf_cfg = 3;
		// Required to be used for full range (+/- 2000 degrees/sec):
		byte DLPF_FS = 1 << 3 | 1 << 4;
		device.write(GYRO_DLPF, (byte) (DLPF_FS | dlpf_cfg));

	}

	private int read(int addressHigh, int addressLow) throws IOException {
		byte high = (byte) device.read((byte) addressHigh);
		byte low = (byte) device.read((byte) addressLow);

		// ByteBuffer bb = ByteBuffer.allocate(2);
		// bb.order(ByteOrder.LITTLE_ENDIAN);
		// bb.put(low);
		// bb.put(high);
		// System.out.println(bb.getShort(0) + " " + ((high << 8) | (low &
		// 0xff)));

		return (high << 8) | (low & 0xff);
	}

	public int whoAmI() {
		int i = 0;
		int result = -1;
		while (result < 0 && i < 10) {
			i++;
			try {
				result = device.read(WHO_AM_I);
			} catch (IOException e) {
				System.out.println("Read failed");
			}
		}
		if (result < 0) {
			throw new IllegalStateException("Failed ten times. Result: "
					+ result);
		}
		return result;
	}

	/**
	 * Reads the temperature from the sensor and returns the value in degrees C * 100.
	 * 
	 * @return temperature in degrees C * 100
	 * @throws IOException
	 */
	public int getTemperature() throws IOException {
		int temp = read(TEMP_OUT_H, TEMP_OUT_L);
		return (3500 + ((temp + 13200) * 100 / 280));
	}

	public int[] getVelocity() throws IOException {
		int[] xyz = new int[3];
		xyz[0] = read(ITG3200_GX_H, ITG3200_GX_L) + offsetX;
		xyz[1] = read(ITG3200_GY_H, ITG3200_GY_L) + offsetY;
		xyz[2] = read(ITG3200_GZ_H, ITG3200_GZ_L) + offsetZ;
		return xyz;
	}
}

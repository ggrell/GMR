package grovepi.i2c_devices;

import java.io.IOException;
import java.nio.charset.Charset;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

/**
 * @author Johannes Bergmann. Changes to align with C# version by Dan Jackson, Newcastle University, 2015.
 */
public class RgbLcdDisplay {

	private static final int DISPLAY_RGB_ADDR = 0x62;
	private static final int DISPLAY_TEXT_ADDR = 0x3e;

	// Commands
	private static final int CLEAR_DISPLAY_CMD = 0x01;
	private static final int CONTROL_DISPLAY_CMD = 0x08;
	private static final int ONE_LINE_CMD = 0x20;
	private static final int TWO_LINES_CMD = 0x28;
	private static final int SHIFT_TEXT_CMD = 0x10;

	// Flags for controlling display
	private static final int DISPLAY_ON = 0x04;
	private static final int CURSOR_ON = 0x02;
	private static final int BLINK_ON = 0x01;

	// Flags for shifting text
	private static final int TEXT_MOVE = 0x08;
	private static final int MOVE_RIGHT = 0x04;
	private static final int MOVE_LEFT = 0x00;

	private final I2CDevice lightDevice;
	private final I2CDevice textDevice;

	private int displayState = 0;

	public RgbLcdDisplay(int rgbAddress, int textAddress) {
		try {
			final I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
			lightDevice = bus.getDevice(rgbAddress);
			textDevice = bus.getDevice(textAddress);
		} catch (IOException e) {
		   throw new RuntimeException(e);
		}
	}

	public RgbLcdDisplay() {
		this(DISPLAY_RGB_ADDR, DISPLAY_TEXT_ADDR);
	}

	public void setBacklightRgb(int r, int g, int b) {
		try {
			lightDevice.write(0, (byte) 0);
			lightDevice.write(1, (byte) 0);
			lightDevice.write(0x08, (byte) 0xaa);
			lightDevice.write(4, (byte) r);
			lightDevice.write(3, (byte) g);
			lightDevice.write(2, (byte) b);
		} catch (IOException e) {
		   throw new RuntimeException(e);
		}
	}

	private void command(int cmd) {
		try {
			textDevice.write(0x80, (byte) cmd);
		} catch (IOException e) {
		   throw new RuntimeException(e);
		}
	}

	private void displayCommand(int prop, boolean on) {
		displayState = (on ? displayState | prop : displayState & ~prop);
		command(CONTROL_DISPLAY_CMD | displayState);
	}

	public void oneLine() {
		command(ONE_LINE_CMD);
	}

	public void twoLines() {
		command(TWO_LINES_CMD);
	}

	/**
	 * Activates or deactivates the display (the text, not the backlight).
	 */
	public void display(boolean on) {
		displayCommand(DISPLAY_ON, on);
	}

	/**
	 * Shows or hides the underline cursor.
	 */
	public void cursor(boolean show) {
		displayCommand(CURSOR_ON, show);
	}

	public void cursorBlink(boolean blink) {
		displayCommand(BLINK_ON, blink);
	}

	public void moveLeft(){
		command(SHIFT_TEXT_CMD | TEXT_MOVE | MOVE_LEFT);
	}

	public void moveRight() {
		command(SHIFT_TEXT_CMD | TEXT_MOVE | MOVE_RIGHT);
	}

	public void setText(String text) {
		try {
			clearText();
			display(true);
			twoLines();
			int count = 0;
			int row = 0;
			for (byte c : text.getBytes(Charset.forName("US-ASCII"))) {
				if (c == '\n' || count == 16) {
					count = 0;
					row += 1;
					if (row == 2) {
						break;
					}
					command(0xc0);
					if (c == '\n') {
						continue;
					}
				}
				count += 1;
				textDevice.write(0x40, c);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void clearText() {
		command(CLEAR_DISPLAY_CMD); // clear display
	}

	/**
	 * Clear text and switches light off.
	 */
	public void shutdown() {
		clearText();
		setBacklightRgb(0, 0, 0);
	}
}
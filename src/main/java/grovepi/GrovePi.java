package grovepi;

import grovepi.common.Delay;

import java.io.IOException;


/**
 * GrovePi+ board.
 *
 * @author Dan Jackson, Newcastle University, 2015.
 *         (originally based on code by Johannes Bergmann, but rewritten to be more like the C# implementation)
 */
public class GrovePi {

    static class Command {
        public static final byte DIGITAL_READ = 1;
        public static final byte DIGITAL_WRITE = 2;
        public static final byte ANALOG_READ = 3;
        public static final byte ANALOG_WRITE = 4;
        public static final byte PIN_MODE = 5;
        public static final byte VERSION = 8;
    }

    private final GrovePiI2CDevice device;

    public GrovePiI2CDevice getDirectAccess() {
        return device;
    }

    public GrovePi() {
        this(GrovePiI2CDevice.createInstanceRuntimeExecption());
    }

    public GrovePi(GrovePiI2CDevice device) {
        this.device = device;
    }


    // [dgj] Added this -- makes more sense than the C# way of making sensors
    private GroveDeviceFactory deviceFactory = null;

    public GroveDeviceFactory getDeviceFactory() {
        if (deviceFactory == null) {
            deviceFactory = new GroveDeviceFactory(this);
        }
        return deviceFactory;
    }


    public String getFirmwareVersion() throws IOException {
        byte[] buffer = new byte[]{Command.VERSION, Constants.UNUSED, Constants.UNUSED, Constants.UNUSED};
        getDirectAccess().read(buffer);
        return "" + buffer[1] + buffer[2] + "." + buffer[3] + "";
    }


    public int digitalRead(int pin) {
        byte[] buffer = new byte[]{(byte) Command.DIGITAL_READ, (byte) pin, Constants.UNUSED, Constants.UNUSED};
        getDirectAccess().write(buffer);
        Delay.milliseconds(20);    // 100 // C# version doesn't do this
        int value = getDirectAccess().read();
        return value;
    }

    public void digitalWrite(int pin, int value) {
        byte[] buffer = new byte[]{(byte) Command.DIGITAL_WRITE, (byte) pin, (byte) value, Constants.UNUSED};
        getDirectAccess().write(buffer);
    }

    public int analogRead(int pin) {
        byte[] buffer = new byte[]{(byte) Command.ANALOG_READ, (byte) pin, Constants.UNUSED, Constants.UNUSED};
        getDirectAccess().write(buffer);
        Delay.milliseconds(20);    // 100 // C# version doesn't do this
        getDirectAccess().read(buffer);
        return Byte.toUnsignedInt(buffer[1]) * 256 + Byte.toUnsignedInt(buffer[2]);
    }

    public void analogWrite(int pin, int value) {
        byte[] buffer = new byte[]{(byte) Command.ANALOG_WRITE, (byte) pin, (byte) value, Constants.UNUSED};
        getDirectAccess().write(buffer);
    }

    public void pinMode(int pin, PinMode mode) {
        byte[] buffer = new byte[]{(byte) Command.PIN_MODE, (byte) pin, (byte) mode.getValue(), Constants.UNUSED};
        getDirectAccess().write(buffer);
    }

}

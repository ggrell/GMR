package grovepi.sensors;

import grovepi.GrovePi;
import grovepi.Constants;
import grovepi.PinMode;
import grovepi.common.Delay;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** @author Dan Jackson, Newcastle University, 2015. */
public class TemperatureAndHumiditySensor
{
    private static final int COMMAND_DHT_TEMPERATURE = 40;
    private static final int MODULE_TYPE = 1;

    public static class Model {
        public static final int DHT11 = 0;
        public static final int DHT22 = 1;
        public static final int DHT21 = 2;
        public static final int DHT2301 = 3;
    }

    protected final GrovePi device;
    protected int pin;
    protected int model;

    protected float temperature = 0.0f;
    protected float humidity = 0.0f;

    public TemperatureAndHumiditySensor(GrovePi device, int pin) {
        this(device, pin, Model.DHT11);
    }

    public TemperatureAndHumiditySensor(GrovePi device, int pin, int model) {
        this.device = device;
        this.pin = pin;
        this.model = model;
        //device.pinMode(pin, PinMode.INPUT);
    }

    public float[] update() {
        byte[] buffer = new byte[] {COMMAND_DHT_TEMPERATURE, (byte)pin, (byte)model, Constants.UNUSED};
        device.getDirectAccess().write(buffer);
        Delay.milliseconds(600);

        byte[] inBuffer = new byte[9];
        device.getDirectAccess().read(inBuffer);

        //for (int i = 0; i < inBuffer.length; i++) { System.out.print(String.format("%02X ", inBuffer[i])); }
        //System.out.println("");

        this.temperature = ByteBuffer.wrap(inBuffer).order(ByteOrder.LITTLE_ENDIAN).getFloat(1);
        this.humidity = ByteBuffer.wrap(inBuffer).order(ByteOrder.LITTLE_ENDIAN).getFloat(5);

        //System.out.println("[" + this.temperature + "," + this.humidity + "]");

        return new float[] { this.temperature, this.humidity };
    }

    public float getTemperature() {
        return this.temperature;
    }

    public float getHumidity() {
        return this.humidity;
    }

}

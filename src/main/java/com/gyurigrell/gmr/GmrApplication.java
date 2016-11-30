package com.gyurigrell.gmr;

import com.ociweb.iot.grove.Grove_LCD_RGB;
import com.ociweb.iot.maker.*;
import com.ociweb.iot.maker.Port;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.logging.Logger;

import static com.ociweb.iot.grove.GroveTwig.*;
import static com.ociweb.iot.maker.Port.*;

@SpringBootApplication
public class GmrApplication {
    private static Logger logger = Logger.getLogger("GmrApplication");

    private static final String LIGHT_TOPIC = "light";
    private static final int BLINK_DELAY_MS = 1000;
    private static final Port RELAY_PORT = D3;
    private static final Port LED_PORT = D4;
    private static final Port BUTTON_PORT = D8;
    private static final Port ANGLE_SENSOR_PORT = A1;
    private static final int MAX_ANGLE_VALUE = 1024;
    private static int angleValue = MAX_ANGLE_VALUE;
    private static CommandChannel lcdChannel;

    public static void main(String[] args) {
        new SpringApplicationBuilder(GmrApplication.class).logStartupInfo(false).run(args);

        DeviceRuntime.run(new IoTSetup() {
            @Override
            public void declareConnections(Hardware hardware) {
                hardware.connect(LED, LED_PORT)
                        .connect(Relay, RELAY_PORT)
                        .connect(Button, BUTTON_PORT)
                        .connect(AngleSensor, ANGLE_SENSOR_PORT);
            }

            @Override
            public void declareBehavior(DeviceRuntime runtime) {
                lcdChannel = runtime.newCommandChannel();
                final CommandChannel blinkerChannel = runtime.newCommandChannel();
                final CommandChannel startupChannel = runtime.newCommandChannel();

                runtime.addPubSubListener((topic, payload) -> {
                    logger.info("PubSub> topic: " + topic);
                    switch (topic.toString()) {
                        case LIGHT_TOPIC:
                            boolean value = payload.readBoolean();
                            blinkerChannel.setValueAndBlock(LED_PORT, value ? 1 : 0, BLINK_DELAY_MS);

                            PayloadWriter writer = blinkerChannel.openTopic(LIGHT_TOPIC);
                            writer.writeBoolean(!value);
                            writer.publish();
                            break;

                        default:

                            break;
                    }

                    Grove_LCD_RGB.commandForText(lcdChannel, topic);
                }).addSubscription(LIGHT_TOPIC);

                runtime.addDigitalListener((port, time, durationMillis, value) -> {
                    logger.info("DIGITAL> port: " + port + " time: " + time + " dur: " + durationMillis + " val: " + value);
                    if (value > 0) {
                        Grove_LCD_RGB.commandForColor(lcdChannel, 255, 0, 0);
                    } else {
                        updateLCDColor();
                    }
                });

                runtime.addAnalogListener((port, time, durationMillis, average, value) -> {
                    logger.info("ANALOG> port: " + port + " time: " + time + " dur: " + durationMillis + " val: " + value);
                    switch (port) {
                        case A1:
                            angleValue = value;
                            updateLCDColor();
                            break;
                    }
                });

                // Initialize the startup settings
                runtime.addStartupListener(
                        () -> {
                            logger.info("DeviceRuntime> startup");
                            updateLCDColor();

                            PayloadWriter writer = startupChannel.openTopic(LIGHT_TOPIC);
                            writer.writeBoolean(true);
                            writer.publish();
                        });
            }
        });
    }

    private static void updateLCDColor() {
        if (lcdChannel != null) {
            int color = (int) ((float) angleValue / (float) MAX_ANGLE_VALUE * 256.0f);
            Grove_LCD_RGB.commandForColor(lcdChannel, color, color, color);
        }
    }
}

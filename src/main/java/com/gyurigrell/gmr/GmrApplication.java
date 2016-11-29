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
    private static final Port LCD_PORT = D8;
    private static final Port BUTTON_PORT = D8;

    //private static Grove_LCD_RGB rgbLcd;

    public static void main(String[] args) {
        new SpringApplicationBuilder(GmrApplication.class).logStartupInfo(false).run(args);
//        SpringApplication.run(GmrApplication.class, args);
        DeviceRuntime.run(new IoTSetup() {
            @Override
            public void declareConnections(Hardware hardware) {
                //rgbLcd = new Grove_LCD_RGB();
                hardware.connect(LED, LED_PORT)
                        .connect(Relay, RELAY_PORT)
                        .connect(Button, BUTTON_PORT);
            }

            @Override
            public void declareBehavior(DeviceRuntime runtime) {
                final CommandChannel lcdChannel = runtime.newCommandChannel();
                //Grove_LCD_RGB.begin(lcdChannel);

                // Timer callback
                final CommandChannel blinkerChannel = runtime.newCommandChannel();
                runtime.addPubSubListener((topic, payload) -> {
                    logger.info("topic: " + topic);
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

                    Grove_LCD_RGB.commandForColor(lcdChannel, 200, 200, 200);
                    Grove_LCD_RGB.commandForText(lcdChannel, topic);
                }).addSubscription(LIGHT_TOPIC);

                runtime.addDigitalListener((connection, time, durationMillis, value) -> {
                    Grove_LCD_RGB.commandForColor(lcdChannel, 255, 0, 0);
                });

                // Initialize the startup settings
                final CommandChannel startupChannel = runtime.newCommandChannel();
                runtime.addStartupListener(
                        () -> {
                            PayloadWriter writer = startupChannel.openTopic(LIGHT_TOPIC);
                            writer.writeBoolean(true);
                            writer.publish();
                        });
            }
        });
    }
}

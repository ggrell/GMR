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

    private static final String PR_OPENED_TOPIC = "pr_opened";

    private static final int BLINK_DELAY_MS = 1000;
    private static final Port RELAY_PORT = D3;
    private static final Port LED_PORT = D4;
    private static final Port TOP_BUTTON1_PORT = D5;
    private static final Port BOTTOM_BUTTON1_PORT = D8;

    public static void main(String[] args) {
        new SpringApplicationBuilder(GmrApplication.class).logStartupInfo(false).run(args);

        DeviceRuntime.run(new IoTSetup() {
            @Override
            public void declareConnections(Hardware hardware) {
                hardware.connect(LED, LED_PORT)
                        .connect(Relay, RELAY_PORT)
                        .connect(Button, BOTTOM_BUTTON1_PORT)
                        .connect(Button, TOP_BUTTON1_PORT);
            }

            @Override
            public void declareBehavior(DeviceRuntime runtime) {
                final CommandChannel lcdChannel = runtime.newCommandChannel();
                final CommandChannel relayChannel = runtime.newCommandChannel();
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

                        case PR_OPENED_TOPIC:
                            relayChannel.setValue(RELAY_PORT, 1);
                            break;
                    }
                }).addSubscription(LIGHT_TOPIC).addSubscription(PR_OPENED_TOPIC);

                runtime.addDigitalListener((port, time, durationMillis, value) -> {
                    logger.info("DIGITAL> port: " + port + " time: " + time + " dur: " + durationMillis + " val: " + value);
                    if (port == TOP_BUTTON1_PORT) {
                        logger.info("Ball reached ");
                        relayChannel.setValue(RELAY_PORT, 0);
                        Grove_LCD_RGB.commandForColor(lcdChannel, 0, 255, 0);
                        Grove_LCD_RGB.commandForTextAndColor(lcdChannel, "Building toolkit-android", 0, 255, 255);
                    } else if (port == BOTTOM_BUTTON1_PORT) {
                        // TODO call through to Jenkins to start build
                        logger.info("Calling Jenkins to start build");
                    }
                });

                // Initialize the startup settings
                runtime.addStartupListener(
                        () -> {
                            logger.info("DeviceRuntime> startup");

                            PayloadWriter writer = startupChannel.openTopic(LIGHT_TOPIC);
                            writer.writeBoolean(true);
                            writer.publish();

                            PayloadWriter writer1 = startupChannel.openTopic(PR_OPENED_TOPIC);
                            writer1.writeBoolean(true);
                            writer1.publish();
                        });
            }
        });
    }
}

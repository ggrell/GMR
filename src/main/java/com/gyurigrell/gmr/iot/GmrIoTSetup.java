package com.gyurigrell.gmr.iot;

import com.ociweb.iot.grove.Grove_LCD_RGB;
import com.ociweb.iot.maker.*;
import com.ociweb.iot.maker.Port;
import reactor.bus.EventBus;

import java.util.logging.Logger;

import static com.ociweb.iot.grove.GroveTwig.*;
import static com.ociweb.iot.maker.Port.*;
import static reactor.bus.selector.Selectors.$;

/**
 */
public class GmrIoTSetup implements IoTSetup {
    private static Logger logger = Logger.getLogger("GmrIoTSetup");

    private static final String LIGHT_TOPIC = "light";

    private static final String PR_OPENED_TOPIC = "pr_opened";

    private static final int BLINK_DELAY_MS = 1000;
    private static final Port RELAY_PORT = D2;
    private static final Port LED_PORT = D4;
    private static final Port TOP_BUTTON1_PORT = D5;
    private static final Port BOTTOM_BUTTON1_PORT = D8;

    private final EventBus eventBus;

    public GmrIoTSetup(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void declareConnections(Hardware hardware) {
        hardware.connect(LED, LED_PORT)
                .connect(Relay, RELAY_PORT)
                .connect(Button, BOTTOM_BUTTON1_PORT)
                .connect(Button, TOP_BUTTON1_PORT);
    }

    @Override
    public void declareBehavior(DeviceRuntime runtime) {

        // Handles the LED blink topic
        final CommandChannel blinkerChannel = runtime.newCommandChannel();
        runtime.addPubSubListener((topic, payload) -> {
            boolean value = payload.readBoolean();
            logger.info("LED " + (value ? "on" : "off"));
            blinkerChannel.setValueAndBlock(LED_PORT, value ? 1 : 0, BLINK_DELAY_MS);

            PayloadWriter writer = blinkerChannel.openTopic(LIGHT_TOPIC);
            writer.writeBoolean(!value);
            writer.publish();
        }).addSubscription(LIGHT_TOPIC);

        final CommandChannel lcdChannel = runtime.newCommandChannel();
        final CommandChannel relayChannel = runtime.newCommandChannel();

        // Handles the PR topic
        runtime.addPubSubListener((topic, payload) -> {
            boolean value = relayChannel.setValue(RELAY_PORT, 1);
            logger.info("PR opened, starting lift. Relay: " + value);
            Grove_LCD_RGB.commandForTextAndColor(lcdChannel, "New PR\ntoolkit-android", 128, 128, 128);
        }).addSubscription(PR_OPENED_TOPIC);

        // Handles the digital port changes
        runtime.addDigitalListener((port, time, durationMillis, value) -> {
//            logger.info("DIGITAL> port: " + port + " time: " + time + " dur: " + durationMillis + " val: " + value);
            if (port == TOP_BUTTON1_PORT) {
                boolean relayValue = relayChannel.setValue(RELAY_PORT, 0);
                logger.info("Ball reached the top of track, stopping lift. Relay: " + relayValue);
            } else if (port == BOTTOM_BUTTON1_PORT) {
                // TODO call through to Jenkins to start build
                logger.info("Ball reached the bottom, calling Jenkins to start build");
                Grove_LCD_RGB.commandForTextAndColor(lcdChannel, "Building\ntoolkit-android", 255, 255, 255);
            }
        });

        // Initialize the startup settings
        final CommandChannel startupChannel = runtime.newCommandChannel();
        runtime.addStartupListener(() -> {
            logger.info("Device Runtime startup");

            PayloadWriter writer = startupChannel.openTopic(LIGHT_TOPIC);
            writer.writeBoolean(true);
            writer.publish();

            Grove_LCD_RGB.clearDisplay(lcdChannel);

            eventBus.on($("pr"), event -> startupChannel.openTopic(PR_OPENED_TOPIC).publish());
        });

    }
}

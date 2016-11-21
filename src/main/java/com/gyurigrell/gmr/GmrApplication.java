package com.gyurigrell.gmr;

import com.ociweb.iot.maker.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.ociweb.iot.grove.GroveTwig.LED;
import static com.ociweb.iot.maker.Port.D4;

@SpringBootApplication
public class GmrApplication {
    private static final String TOPIC = "light";
    private static final int PAUSE = 500;
    public static final Port LED_PORT = D4;

    public static void main(String[] args) {
        SpringApplication.run(GmrApplication.class, args);
        DeviceRuntime.run(new IoTSetup() {
            @Override
            public void declareConnections(Hardware hardware) {
                hardware.connect(LED, LED_PORT);
            }

            @Override
            public void declareBehavior(DeviceRuntime runtime) {
                final CommandChannel blinkerChannel = runtime.newCommandChannel();
                runtime.addPubSubListener((topic, payload) -> {

                    boolean value = payload.readBoolean();
                    blinkerChannel.setValueAndBlock(LED_PORT, value ? 1 : 0, PAUSE);
                    PayloadWriter writer = blinkerChannel.openTopic(TOPIC);
                    writer.writeBoolean(!value);
                    writer.publish();

                }).addSubscription(TOPIC);

                final CommandChannel startupChannel = runtime.newCommandChannel();
                runtime.addStartupListener(
                        () -> {
                            PayloadWriter writer = startupChannel.openTopic(TOPIC);
                            writer.writeBoolean(true);
                            writer.publish();
                        });
            }
        });
    }
}

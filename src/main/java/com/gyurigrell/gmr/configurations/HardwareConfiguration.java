package com.gyurigrell.gmr.configurations;

import com.gyurigrell.gmr.iot.GmrIoTSetup;
import com.ociweb.iot.maker.DeviceRuntime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.bus.EventBus;

/**
 */
@Configuration
public class HardwareConfiguration {
    @Bean
    DeviceRuntime createDeviceRuntime(EventBus eventBus) {
        return DeviceRuntime.run(new GmrIoTSetup(eventBus));
    }
}

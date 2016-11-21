package com.gyurigrell.gmr.configurations;

import grovepi.GrovePi;
import grovepi.GrovePiI2CDevice;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 */
@Configuration
public class CustomConfiguration {
    @Bean
    public GrovePi produceGrovePi() throws IOException {
        return new GrovePi(GrovePiI2CDevice.createInstance("fake", 0));
    }
}

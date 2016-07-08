package com.gyurigrell.gmr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;

@SpringBootApplication
public class GmrApplication {
	public static void main(String[] args) {
		SpringApplication.run(GmrApplication.class, args);
	}

	private static final String EOL = "\n";
	private static final int SIGNATURE_LENGTH = 45;
	private final String secret;

	@Value("${build.version}")
	private String version;

	@Value("${build.commit}")
	private String commitId;

	public GmrApplication() {
		this(System.getenv("SECRET_KEY"));
	}

	public GmrApplication(String secret) {
//		Objects.requireNonNull(secret, "No secret given.");
		this.secret = secret;
	}

	@RequestMapping("/")
	@ResponseBody
	public String home() {
		return "up and running!\n";
	}
}

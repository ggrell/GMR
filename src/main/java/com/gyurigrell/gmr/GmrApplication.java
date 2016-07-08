package com.gyurigrell.gmr;

import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.MessageDigest;
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

    @RequestMapping(value = "/github-webhook", method = RequestMethod.POST)
    public ResponseEntity<String> handle(@RequestHeader("X-Hub-Signature") String signature,
                                         @RequestBody String payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Webhook-Version", String.format("%s/%s", version, commitId));

        if (signature == null) {
            return new ResponseEntity<>("No signature given." + EOL, headers,
                    HttpStatus.BAD_REQUEST);
        }

        String computed = String.format("sha1=%s", HmacUtils.hmacSha1Hex(secret, payload));
        boolean invalidLength = signature.length() != SIGNATURE_LENGTH;

        if (invalidLength || !constantTimeCompare(signature, computed)) {
            return new ResponseEntity<>("Invalid signature." + EOL, headers,
                    HttpStatus.UNAUTHORIZED);
        }

        int bytes = payload.getBytes().length;
        StringBuilder message = new StringBuilder();
        message.append("Signature OK.").append(EOL);
        message.append(String.format("Received %d bytes.", bytes)).append(EOL);
        return new ResponseEntity<>(message.toString(), headers, HttpStatus.OK);
    }

    public static boolean constantTimeCompare(String a, String b) {
        return MessageDigest.isEqual(a.getBytes(), b.getBytes());
    }
}

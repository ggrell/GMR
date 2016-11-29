package com.gyurigrell.gmr.controllers;

import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.security.MessageDigest;

/**
 */
@Controller
public class GithubWebhookController {
    private static final String EOL = "\n";

    private static final int SIGNATURE_LENGTH = 45;

    private final String secret;

//    private final GrovePi grovePi;

    //	@Value("${build.version}")
    private String version;

    //	@Value("${build.commit}")
    private String commitId;

    public GithubWebhookController() {
        this(System.getenv("SECRET_KEY"));
    }

    public GithubWebhookController(String secret) {
//		Objects.requireNonNull(secret, "No secret given.");
//        this.grovePi = grovePi;
        this.secret = secret;
    }

    @PostMapping("/github-webhook")
    public ResponseEntity<String> webhook(@RequestHeader("X-Hub-Signature") String signature,
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

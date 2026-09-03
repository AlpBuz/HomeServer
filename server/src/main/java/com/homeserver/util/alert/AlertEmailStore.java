package com.homeserver.util.alert;

import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermissions;

// Persists the server owner's alert email outside of the source tree (data/, gitignored)
// so it survives restarts, without ever routing it through logs or version control.
@Component
public class AlertEmailStore {

    private static final Logger log = LoggerFactory.getLogger(AlertEmailStore.class);

    private final Path storePath;
    private volatile String email;

    public AlertEmailStore(@Value("${alert.email.store-path:data/alert-owner-email.txt}") String storePath) {
        this.storePath = Paths.get(storePath);
    }

    @PostConstruct
    public void load() {
        try {
            if (Files.exists(storePath)) {
                String stored = Files.readString(storePath, StandardCharsets.UTF_8).trim();
                if (!stored.isEmpty()) {
                    this.email = stored;
                }
            }
        } catch (IOException e) {
            log.error("Failed to load stored alert email", e);
        }
    }

    public synchronized void save(String rawEmail) {
        String normalized = validate(rawEmail);

        try {
            Path parent = storePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
                restrictToOwner(parent, true);
            }

            Files.writeString(storePath, normalized, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            restrictToOwner(storePath, false);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to persist alert email", e);
        }

        this.email = normalized;
    }

    public synchronized void clear() {
        try {
            Files.deleteIfExists(storePath);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to remove stored alert email", e);
        }
        this.email = null;
    }

    public String get() {
        return email;
    }

    public boolean isSet() {
        return email != null;
    }

    // Safe to log or return over the API: hides everything but the first character of the local part.
    public static String mask(String email) {
        if (email == null) {
            return null;
        }
        int at = email.indexOf('@');
        if (at <= 1) {
            return "***" + email.substring(Math.max(at, 0));
        }
        return email.charAt(0) + "***" + email.substring(at);
    }

    // Parsed via InternetAddress rather than a hand-rolled regex, so malformed input
    // (including stray CR/LF used for SMTP header injection) is rejected up front,
    // before it can ever reach a mail message's To/From header.
    private static String validate(String rawEmail) {
        if (rawEmail == null) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        String trimmed = rawEmail.trim();
        if (trimmed.isEmpty() || trimmed.contains("\n") || trimmed.contains("\r")) {
            throw new IllegalArgumentException("Invalid email address");
        }
        try {
            InternetAddress address = new InternetAddress(trimmed);
            address.validate();
            return address.getAddress();
        } catch (AddressException e) {
            throw new IllegalArgumentException("Invalid email address");
        }
    }

    private static void restrictToOwner(Path path, boolean isDirectory) {
        try {
            Files.setPosixFilePermissions(path,
                    PosixFilePermissions.fromString(isDirectory ? "rwx------" : "rw-------"));
        } catch (UnsupportedOperationException | IOException e) {
            // Not a POSIX filesystem - best effort only, still fine outside of git.
        }
    }
}

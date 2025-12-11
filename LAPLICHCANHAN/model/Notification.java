package LAPLICHCANHAN.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Notification {
	private UUID id;
    private UUID userId;
    private String message;
    private LocalDateTime timestamp;
    private boolean read;

    public Notification(UUID userId, String message, LocalDateTime timestamp) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.message = message;
        this.timestamp = timestamp;
    }

    // getters / setters

}

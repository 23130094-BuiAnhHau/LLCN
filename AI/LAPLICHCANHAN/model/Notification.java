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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return read;
    }

}

package LAPLICHCANHAN.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Event {
    private UUID id;
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
    private String recurrenceRule;
    private boolean fixed = true;

    public Event(String title, LocalDateTime start, LocalDateTime end, boolean fixed) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("title must not be empty");
        }
        if (start == null || end == null) {
            throw new IllegalArgumentException("start/end must not be null");
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("end must be after start");
        }

        this.id = UUID.randomUUID();
        this.title = title;
        this.start = start;
        this.end = end;
        this.fixed = fixed;
        this.recurrenceRule = null;
    }

    public Event(String title, LocalDateTime start, LocalDateTime end, boolean fixed, String recurrenceRule) {
        this(title, start, end, fixed);
        this.recurrenceRule = (recurrenceRule == null || recurrenceRule.trim().isEmpty())
                ? null
                : recurrenceRule.trim();
    }

    // getters / setters
    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        if (start == null)
            return;
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        if (end == null)
            return;
        if (!end.isAfter(this.start)) {
            throw new IllegalArgumentException("end must be after start");
        }
        this.end = end;
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    public String getRecurrenceRule() {
        return recurrenceRule;
    }

    public void setRecurrenceRule(String recurrenceRule) {
        this.recurrenceRule = (recurrenceRule == null || recurrenceRule.trim().isEmpty())
                ? null
                : recurrenceRule.trim();
    }

    @Override
    public String toString() {
        String r = (recurrenceRule == null) ? "" : (" RRULE=" + recurrenceRule);
        return "Event{" +
                "title='" + title + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", fixed=" + fixed +
                r +
                '}';
    }

}

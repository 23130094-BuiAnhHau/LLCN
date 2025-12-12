package LAPLICHCANHAN.model;

import java.util.UUID;

public class ScheduleEntry {
    public enum EntryType {
        TASK, EVENT
    }

    private UUID id;
    private EntryType type;
    private TimeSlot slot;
    private Task task; // nếu type == TASK
    private Event event; // nếu type == EVENT

    // dùng để thêm vào lịch thật
    public ScheduleEntry(EntryType type, TimeSlot slot, Task task, Event event) {
        if (slot == null)
            throw new IllegalArgumentException("slot must not be null");

        if (type == EntryType.TASK && task == null)
            throw new IllegalArgumentException("Task entry requires task != null");

        if (type == EntryType.EVENT && event == null)
            throw new IllegalArgumentException("Event entry requires event != null");

        this.id = UUID.randomUUID();
        this.type = type;
        this.slot = slot;
        this.task = task;
        this.event = event;
    }

    // dùng cho clim Himding
    public ScheduleEntry(UUID id, EntryType type, TimeSlot slotCopy, Task taskRef, Event eventRef) {
        this.id = id;
        this.type = type;
        this.slot = slotCopy;
        this.task = taskRef;
        this.event = eventRef;
    }

    // getters / setters
    public UUID getId() {
        return id;
    }

    public EntryType getType() {
        return type;
    }

    public void setSlot(TimeSlot slot) {
        this.slot = slot;
    }

    public TimeSlot getSlot() {
        return slot;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Task getTask() {
        return task;
    }

    public Event getEvent() {
        return event;
    }

    // Copy lịch để khi chạy Clim Himding thì lịch sẽ không bị thay đổi
    public ScheduleEntry deepCopy() {
        TimeSlot slotCopy = new TimeSlot(
                this.slot.getStart(),
                this.slot.getEnd());

        return new ScheduleEntry(
                this.id,
                this.type,
                slotCopy,
                this.task,
                this.event);
    }

    // Kiểm tra xung đột giữa hai entry
    public boolean conflictsWith(ScheduleEntry other) {
        if (other == null)
            return false;
        if (this.slot == null || other.slot == null)
            return false;

        return this.slot.overlaps(other.slot);
    }

    // In lịch
    public String toString() {
        String label = (type == EntryType.TASK)
                ? ("TASK: " + task.getTitle())
                : ("EVENT: " + event.getTitle());

        return "[" + label + "] " +
                slot.getStart().toString() +
                " -> " +
                slot.getEnd().toString();
    }
}

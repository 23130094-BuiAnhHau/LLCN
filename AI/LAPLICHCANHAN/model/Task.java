package LAPLICHCANHAN.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Task: đại diện 1 công việc cần xếp vào lịch.
 */
public class Task {

    private UUID id;
    private String title;
    private String description;
    private int durationMinutes; // thời lượng cần thực hiện (phút)
    private LocalDateTime deadline; // nullable
    private Priority priority;

    // danh sách slot mà scheduler có thể dùng (tùy implementation)
    private List<TimeSlot> availableSlots = new ArrayList<TimeSlot>();

    // user muốn làm vào những khoảng này (ưu tiên đặt vào)
    private List<TimeSlot> preferredSlots = new ArrayList<TimeSlot>();

    // nếu true -> scheduler có thể đặt task ngoài working hours của user
    private boolean allowOutsideWorkingHours = false;

    private Status status = Status.TODO;
    private int estimatedEffort; // optional, đơn vị điểm

    public Task(String title, int durationMinutes, Priority priority) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("title must not be empty");
        }
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("durationMinutes must be positive");
        }
        if (priority == null) {
            throw new IllegalArgumentException("priority must not be null");
        }
        this.id = UUID.randomUUID();
        this.title = title;
        this.durationMinutes = durationMinutes;
        this.priority = priority;
    }

    public Task(UUID id, String title, int durationMinutes, LocalDateTime deadline, Priority priority) {
        this(title, durationMinutes, priority);
        this.id = (id == null) ? UUID.randomUUID() : id;
        this.deadline = deadline;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty())
            return;
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        if (durationMinutes <= 0)
            return;
        this.durationMinutes = durationMinutes;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        if (priority != null)
            this.priority = priority;
    }

    public List<TimeSlot> getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(List<TimeSlot> availableSlots) {
        if (availableSlots == null)
            return;
        this.availableSlots = availableSlots;
    }

    public List<TimeSlot> getPreferredSlots() {
        return preferredSlots;
    }

    /**
     * Thêm preferred slot (người dùng muốn làm vào khoảng này)
     */
    public void addPreferredSlot(TimeSlot slot) {
        if (slot == null)
            return;
        this.preferredSlots.add(slot);
    }

    /**
     * Xóa tất cả preferred slot
     */
    public void clearPreferredSlots() {
        this.preferredSlots.clear();
    }

    public boolean isAllowOutsideWorkingHours() {
        return allowOutsideWorkingHours;
    }

    public void setAllowOutsideWorkingHours(boolean allowOutsideWorkingHours) {
        this.allowOutsideWorkingHours = allowOutsideWorkingHours;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if (status != null)
            this.status = status;
    }

    public int getEstimatedEffort() {
        return estimatedEffort;
    }

    public void setEstimatedEffort(int estimatedEffort) {
        this.estimatedEffort = estimatedEffort;
    }

    /**
     * Kiểm tra task có hợp lệ để đưa vào scheduler hay không
     */
    public boolean isSchedulable() {
        return this.durationMinutes > 0;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", duration=" + durationMinutes +
                ", priority=" + priority +
                ", deadline=" + deadline +
                ", allowOutside=" + allowOutsideWorkingHours +
                '}';
    }
}

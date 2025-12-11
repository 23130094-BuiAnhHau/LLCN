package LAPLICHCANHAN.model;

import java.util.*;

public class User {
    private UUID id;
    private String name;
    private String email;
    private List<TimeSlot> workingHours = new ArrayList<>(); // ví dụ 08:00-22:00 từng ngày

    public User(String name, String email) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
    }

    // getters / setters
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<TimeSlot> getWorkingHours() {
        return workingHours;
    }

    public void addWorkingHour(TimeSlot slot) {
        if (slot != null)
            workingHours.add(slot);
    }

    public void clearWorkingHours() {
        workingHours.clear();
    }
}

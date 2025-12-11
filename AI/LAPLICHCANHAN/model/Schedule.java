package LAPLICHCANHAN.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Schedule {

    private UUID id;
    private UUID userId;
    private List<ScheduleEntry> entries = new ArrayList<ScheduleEntry>();

    public Schedule(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
    }

    public Schedule(UUID id, UUID userId, List<ScheduleEntry> entries) {
        this.id = id;
        this.userId = userId;
        this.entries = entries;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public List<ScheduleEntry> getEntries() {
        return entries;
    }

    public void addEntry(ScheduleEntry entry) {
        if (entry != null)
            entries.add(entry);
    }

    public void removeEntry(UUID entryId) {
        int i = 0;
        while (i < entries.size()) {
            if (entries.get(i).getId().equals(entryId)) {
                entries.remove(i);
                continue;
            }
            i++;
        }
    }

    public List<TimeSlot> getFreeSlots(LocalDate startDate,
            LocalDate endDate,
            List<TimeSlot> workingHours) {

        List<TimeSlot> result = new ArrayList<TimeSlot>();

        if (startDate == null || endDate == null) {
            return result;
        }
        if (endDate.isBefore(startDate)) {
            return result;
        }

        LocalDate day = startDate;
        while (!day.isAfter(endDate)) {

            List<TimeSlot> dayWorking = new ArrayList<TimeSlot>();

            if (workingHours == null || workingHours.isEmpty()) {

                LocalDateTime s = day.atTime(6, 0);
                LocalDateTime e = day.atTime(22, 0);
                dayWorking.add(new TimeSlot(s, e));
            } else {

                int wi = 0;
                while (wi < workingHours.size()) {
                    TimeSlot wh = workingHours.get(wi);

                    LocalDate whStartDate = wh.getStart().toLocalDate();
                    LocalDate whEndDate = wh.getEnd().toLocalDate();
                    if ((!day.isBefore(whStartDate)) && (!day.isAfter(whEndDate))) {

                        LocalDateTime clipStart = wh.getStart();
                        if (clipStart.toLocalDate().isBefore(day)) {
                            clipStart = LocalDateTime.of(day, clipStart.toLocalTime());
                        }
                        LocalDateTime clipEnd = wh.getEnd();
                        if (clipEnd.toLocalDate().isAfter(day)) {
                            clipEnd = LocalDateTime.of(day, clipEnd.toLocalTime());
                        }
                        if (clipEnd.isAfter(clipStart)) {
                            dayWorking.add(new TimeSlot(clipStart, clipEnd));
                        }
                    }
                    wi++;
                }
            }

            List<TimeSlot> busyOnDay = new ArrayList<TimeSlot>();
            int ei = 0;
            while (ei < entries.size()) {
                ScheduleEntry se = entries.get(ei);
                TimeSlot slot = se.getSlot();
                if (slot == null) {
                    ei++;
                    continue;
                }

                LocalDate slotStartDate = slot.getStart().toLocalDate();
                LocalDate slotEndDate = slot.getEnd().toLocalDate();
                if ((!day.isBefore(slotStartDate)) && (!day.isAfter(slotEndDate))) {
                    LocalDateTime clipStart = slot.getStart();
                    if (clipStart.toLocalDate().isBefore(day)) {
                        clipStart = LocalDateTime.of(day, clipStart.toLocalTime());
                    }
                    LocalDateTime clipEnd = slot.getEnd();
                    if (clipEnd.toLocalDate().isAfter(day)) {
                        clipEnd = LocalDateTime.of(day, clipEnd.toLocalTime());
                    }

                    if (clipEnd.isAfter(clipStart)) {
                        busyOnDay.add(new TimeSlot(clipStart, clipEnd));
                    }
                }
                ei++;
            }

            int w = 0;
            while (w < dayWorking.size()) {
                TimeSlot wk = dayWorking.get(w);

                List<TimeSlot> fragments = new ArrayList<TimeSlot>();
                fragments.add(new TimeSlot(wk.getStart(), wk.getEnd()));

                int bi = 0;
                while (bi < busyOnDay.size()) {
                    TimeSlot busy = busyOnDay.get(bi);
                    List<TimeSlot> newFragments = new ArrayList<TimeSlot>();

                    int fi = 0;
                    while (fi < fragments.size()) {
                        TimeSlot frag = fragments.get(fi);

                        if (!frag.overlaps(busy)) {
                            newFragments.add(new TimeSlot(frag.getStart(), frag.getEnd()));
                        } else {

                            LocalDateTime fragStart = frag.getStart();
                            LocalDateTime fragEnd = frag.getEnd();
                            LocalDateTime busyStart = busy.getStart();
                            LocalDateTime busyEnd = busy.getEnd();

                            if (fragStart.isBefore(busyStart)) {
                                LocalDateTime beforeEnd = (busyStart.isBefore(fragEnd) ? busyStart : fragEnd);
                                if (beforeEnd.isAfter(fragStart)) {
                                    newFragments.add(new TimeSlot(fragStart, beforeEnd));
                                }
                            }

                            if (busyEnd.isBefore(fragEnd)) {
                                LocalDateTime afterStart = (busyEnd.isAfter(fragStart) ? busyEnd : fragStart);
                                if (fragEnd.isAfter(afterStart)) {
                                    newFragments.add(new TimeSlot(afterStart, fragEnd));
                                }
                            }
                        }

                        fi++;
                    }

                    fragments = newFragments;
                    bi++;
                    if (fragments.isEmpty())
                        break;
                }
                int rfi = 0;
                while (rfi < fragments.size()) {
                    TimeSlot f = fragments.get(rfi);
                    if (f.getEnd().isAfter(f.getStart())) {
                        result.add(f);
                    }
                    rfi++;
                }

                w++;
            }

            day = day.plusDays(1);
        }

        return result;
    }

    /**
     * Tạo bản sao Schedule phục vụ hill climbing.
     */
    public Schedule deepCopy() {
        List<ScheduleEntry> copied = new ArrayList<ScheduleEntry>();
        int i = 0;
        while (i < entries.size()) {
            copied.add(entries.get(i).deepCopy());
            i++;
        }
        return new Schedule(this.id, this.userId, copied);
    }
}

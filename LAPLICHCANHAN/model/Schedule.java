package LAPLICHCANHAN.model;

import java.time.LocalDate;
import java.util.*;

public class Schedule {
    private UUID id;
    private UUID userId;
    private List<ScheduleEntry> entries = new ArrayList<>();

    public Schedule(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
    }

    public Schedule(UUID id, UUID userId, List<ScheduleEntry> entries) {
        this.id = id;
        this.userId = userId;
        this.entries = entries;
    }

    public List<ScheduleEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    // Thêm entry vào lịch
    public void addEntry(ScheduleEntry entry) {
        entries.add(entry);
    }

    // Xóa entry theo id
    public void removeEntry(UUID entryId) {
        int i = 0;
        while (i < entries.size()) {
            ScheduleEntry e = entries.get(i);
            if (e.getId().equals(entryId)) {
                entries.remove(i);
                continue;
            }
            i++;
        }
    }

    // Tìm khoảng thời gian trống
    public List<TimeSlot> getFreeSlots(LocalDate startDate, LocalDate endDate, List<TimeSlot> workingHours) {

        List<TimeSlot> result = new ArrayList<TimeSlot>();

        if (workingHours == null || workingHours.isEmpty()) {
            // Nếu user chưa set working hour → mặc định làm từ 8h–22h
            workingHours = new ArrayList<TimeSlot>();
        }

        // gom tất cả Entries trong khoảng ngày
        List<ScheduleEntry> busyEntries = new ArrayList<ScheduleEntry>();
        for (int i = 0; i < entries.size(); i++) {
            ScheduleEntry e = entries.get(i);
            LocalDate d = e.getSlot().getStart().toLocalDate();
            if ((!d.isBefore(startDate)) && (!d.isAfter(endDate))) {
                busyEntries.add(e);
            }
        }

        // Sắp xếp theo thời gian bắt đầu (truyền thống, Comparator ẩn danh)
        Collections.sort(busyEntries, new Comparator<ScheduleEntry>() {
            @Override
            public int compare(ScheduleEntry a, ScheduleEntry b) {
                return a.getSlot().getStart().compareTo(b.getSlot().getStart());
            }
        });

        // Mặc định working hour: 06:00 - 22:00 nếu user không có workingHours
        TimeSlot full;
        if (busyEntries.isEmpty()) {
            full = new TimeSlot(startDate.atTime(6, 0), startDate.atTime(22, 0));
        } else {
            LocalDateTimeRefPair firstStart = new LocalDateTimeRefPair(busyEntries.get(0).getSlot().getStart());
            full = new TimeSlot(firstStart.withHour(6).withMinute(0), firstStart.withHour(22).withMinute(0));
        }

        // Nếu không có lịch bận -> toàn bộ là free
        if (busyEntries.isEmpty()) {
            result.add(full);
            return result;
        }

        // 1. slot từ 6h -> entry đầu tiên
        TimeSlot first = busyEntries.get(0).getSlot();
        if (full.getStart().isBefore(first.getStart())) {
            result.add(new TimeSlot(full.getStart(), first.getStart()));
        }

        // 2. các slot giữa các entry
        for (int i = 0; i < busyEntries.size() - 1; i++) {
            TimeSlot a = busyEntries.get(i).getSlot();
            TimeSlot b = busyEntries.get(i + 1).getSlot();

            if (a.getEnd().isBefore(b.getStart())) {
                result.add(new TimeSlot(a.getEnd(), b.getStart()));
            }
        }

        // 3. slot từ entry cuối -> 22h
        TimeSlot last = busyEntries.get(busyEntries.size() - 1).getSlot();
        if (last.getEnd().isBefore(full.getEnd())) {
            result.add(new TimeSlot(last.getEnd(), full.getEnd()));
        }

        return result;
    }

    // Tạo bản sao độc lập
    public Schedule deepCopy() {
        List<ScheduleEntry> newList = new ArrayList<ScheduleEntry>();

        for (int i = 0; i < entries.size(); i++) {
            ScheduleEntry e = entries.get(i);
            newList.add(e.deepCopy());
        }

        return new Schedule(
                this.id,
                this.userId,
                newList);
    }

}

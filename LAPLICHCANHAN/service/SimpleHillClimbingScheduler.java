package LAPLICHCANHAN.service;

import LAPLICHCANHAN.model.*;
import LAPLICHCANHAN.model.ScheduleEntry.EntryType;

import java.time.LocalDateTime;
import java.util.*;

public class SimpleHillClimbingScheduler implements Scheduler {

    private int maxIterations = 300;

    @Override
    public Schedule generateInitialSchedule(List<Task> tasks, List<Event> events, User user) {
        Schedule schedule = new Schedule(user.getId());

        // 1. Thêm sự kiện cố định vào trước
        for (Event ev : events) {
            ScheduleEntry entry = new ScheduleEntry(
                    EntryType.EVENT,
                    new TimeSlot(ev.getStart(), ev.getEnd()),
                    null,
                    ev);
            schedule.addEntry(entry);
        }

        // 2. Sắp xếp Task theo độ ưu tiên + deadline
        tasks.sort((a, b) -> {
            int p = b.getPriority().ordinal() - a.getPriority().ordinal();
            if (p != 0)
                return p;
            if (a.getDeadline() == null)
                return 1;
            if (b.getDeadline() == null)
                return -1;
            return a.getDeadline().compareTo(b.getDeadline());
        });

        // 3. đặt mỗi task vào slot trống đầu tiên tìm được
        LocalDateTime start = LocalDateTime.now().withHour(8).withMinute(0);
        LocalDateTime end = LocalDateTime.now().withHour(22).withMinute(0);

        List<TimeSlot> freeSlots = schedule.getFreeSlots(start.toLocalDate(), end.toLocalDate(),
                user.getWorkingHours());

        for (Task t : tasks) {
            for (TimeSlot slot : freeSlots) {
                if (slot.lengthMinutes() >= t.getDurationMinutes()) {
                    // tạo slot nhỏ cho task
                    TimeSlot used = new TimeSlot(slot.getStart(),
                            slot.getStart().plusMinutes(t.getDurationMinutes()));

                    ScheduleEntry entry = new ScheduleEntry(
                            EntryType.TASK,
                            used,
                            t,
                            null);

                    schedule.addEntry(entry);
                    break;
                }
            }
        }

        return schedule;
    }

    @Override
    public Schedule hillClimb(Schedule schedule) {

        Schedule current = schedule;
        double currentScore = score(current);

        for (int i = 0; i < maxIterations; i++) {

            Schedule neighbor = generateNeighbor(current);
            double neighborScore = score(neighbor);

            if (neighborScore > currentScore) {
                current = neighbor;
                currentScore = neighborScore;
            }
        }

        return current;
    }

    /**
     * Sinh lịch hàng xóm bằng cách:
     * - hoán đổi 2 task
     * - dịch một task sang slot mới
     */
    private Schedule generateNeighbor(Schedule schedule) {
        Schedule copy = schedule.deepCopy();
        Random rand = new Random();

        List<ScheduleEntry> entries = copy.getEntries();

        if (entries.size() < 2)
            return copy;

        int i = rand.nextInt(entries.size());
        int j = rand.nextInt(entries.size());

        ScheduleEntry a = entries.get(i);
        ScheduleEntry b = entries.get(j);

        // chỉ hoán đổi nếu cả 2 cùng là task
        if (a.getTask() != null && b.getTask() != null) {
            TimeSlot tmp = a.getSlot();
            a.setSlot(tmp);
            b.getSlot();
            b.setSlot(tmp);
        }

        return copy;
    }

    @Override
    public double score(Schedule schedule) {
        double score = 0;

        // 1. Ưu tiên task có priority cao
        for (ScheduleEntry e : schedule.getEntries()) {
            if (e.getTask() != null) {
                score += (e.getTask().getPriority().ordinal() + 1) * 10;
            }
        }

        // 2. Trừ điểm nếu có xung đột
        for (int i = 0; i < schedule.getEntries().size(); i++) {
            for (int j = i + 1; j < schedule.getEntries().size(); j++) {
                if (schedule.getEntries().get(i).conflictsWith(schedule.getEntries().get(j))) {
                    score -= 50;
                }
            }
        }

        return score;
    }
}

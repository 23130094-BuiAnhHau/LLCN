package LAPLICHCANHAN.service;

import LAPLICHCANHAN.model.*;
import LAPLICHCANHAN.model.ScheduleEntry.EntryType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class SimpleHillClimbingScheduler implements Scheduler {

    private int maxIterations = 300;
    private final Random rand = new Random();

    @Override
    public Schedule generateInitialSchedule(List<Task> tasks, List<Event> events, User user) {

        LocalDate start = LocalDate.now();
        LocalDate end = findMaxDeadline(tasks);
        if (!end.isAfter(start)) {
            end = start.plusDays(6);
        }
        return generateScheduleForRange(tasks, events, user, start, end);
    }

    public Schedule generateScheduleForRange(List<Task> tasks,
            List<Event> events,
            User user,
            LocalDate start,
            LocalDate end) {

        if (start == null || end == null || start.isAfter(end)) {
            throw new IllegalArgumentException("Invalid date range");
        }

        Schedule schedule = new Schedule(user.getId());

        // 1. add events first
        if (events != null) {
            int ei = 0;
            while (ei < events.size()) {
                Event ev = events.get(ei);
                schedule.addEntry(new ScheduleEntry(
                        EntryType.EVENT,
                        new TimeSlot(ev.getStart(), ev.getEnd()),
                        null,
                        ev));
                ei++;
            }
        }

        // 2. prepare sorted task list (priority desc, deadline asc)
        List<Task> taskList = new ArrayList<Task>();
        if (tasks != null) {
            int ti = 0;
            while (ti < tasks.size()) {
                taskList.add(tasks.get(ti));
                ti++;
            }
        }
        // sort without lambda
        Collections.sort(taskList, new Comparator<Task>() {
            @Override
            public int compare(Task a, Task b) {
                int p = b.getPriority().ordinal() - a.getPriority().ordinal();
                if (p != 0)
                    return p;
                if (a.getDeadline() == null)
                    return 1;
                if (b.getDeadline() == null)
                    return -1;
                return a.getDeadline().compareTo(b.getDeadline());
            }
        });

        List<TimeSlot> freeSlots = schedule.getFreeSlots(start, end, user.getWorkingHours());

        int ti = 0;
        while (ti < taskList.size()) {
            Task t = taskList.get(ti);
            boolean placed = false;
            List<TimeSlot> tAvail = t.getAvailableSlots();
            if (tAvail != null && !tAvail.isEmpty()) {
                int ai = 0;
                while (ai < tAvail.size() && !placed) {
                    TimeSlot avail = tAvail.get(ai);

                    int fi = 0;
                    while (fi < freeSlots.size() && !placed) {
                        TimeSlot free = freeSlots.get(fi);

                        LocalDateTime candStart = free.getStart().isAfter(avail.getStart()) ? free.getStart()
                                : avail.getStart();
                        LocalDateTime candEnd = candStart.plusMinutes(t.getDurationMinutes());

                        if ((!candEnd.isAfter(free.getEnd())) && (!candEnd.isAfter(avail.getEnd()))) {

                            TimeSlot used = new TimeSlot(candStart, candEnd);
                            schedule.addEntry(new ScheduleEntry(EntryType.TASK, used, t, null));

                            freeSlots.remove(fi);
                            if (free.getStart().isBefore(used.getStart())) {
                                freeSlots.add(new TimeSlot(free.getStart(), used.getStart()));
                            }
                            if (used.getEnd().isBefore(free.getEnd())) {
                                freeSlots.add(new TimeSlot(used.getEnd(), free.getEnd()));
                            }
                            placed = true;
                            break;
                        }
                        fi++;
                    }
                    ai++;
                }
            }

            if (!placed) {
                int fi = 0;
                while (fi < freeSlots.size() && !placed) {
                    TimeSlot free = freeSlots.get(fi);
                    if (free.lengthMinutes() >= t.getDurationMinutes()) {
                        LocalDateTime candStart = free.getStart();
                        LocalDateTime candEnd = candStart.plusMinutes(t.getDurationMinutes());

                        if (t.getDeadline() != null && candEnd.isAfter(t.getDeadline())) {

                            fi++;
                            continue;
                        }
                        TimeSlot used = new TimeSlot(candStart, candEnd);
                        schedule.addEntry(new ScheduleEntry(EntryType.TASK, used, t, null));

                        freeSlots.remove(fi);
                        if (free.getStart().isBefore(used.getStart())) {
                            freeSlots.add(new TimeSlot(free.getStart(), used.getStart()));
                        }
                        if (used.getEnd().isBefore(free.getEnd())) {
                            freeSlots.add(new TimeSlot(used.getEnd(), free.getEnd()));
                        }
                        placed = true;
                        break;
                    }
                    fi++;
                }
            }

            if (!placed && t.getDeadline() != null) {
                int fi = 0;
                while (fi < freeSlots.size() && !placed) {
                    TimeSlot free = freeSlots.get(fi);
                    if (free.lengthMinutes() >= t.getDurationMinutes()) {
                        TimeSlot used = new TimeSlot(free.getStart(),
                                free.getStart().plusMinutes(t.getDurationMinutes()));
                        schedule.addEntry(new ScheduleEntry(EntryType.TASK, used, t, null));
                        freeSlots.remove(fi);
                        if (free.getStart().isBefore(used.getStart())) {
                            freeSlots.add(new TimeSlot(free.getStart(), used.getStart()));
                        }
                        if (used.getEnd().isBefore(free.getEnd())) {
                            freeSlots.add(new TimeSlot(used.getEnd(), free.getEnd()));
                        }
                        placed = true;
                        break;
                    }
                    fi++;
                }
            }

            ti++;
        }

        return schedule;
    }

    public Schedule generateWeeklySchedule(List<Task> tasks, List<Event> events, User user, LocalDate weekStart) {
        if (weekStart == null)
            throw new IllegalArgumentException("weekStart null");
        LocalDate weekEnd = weekStart.plusDays(6);
        return generateScheduleForRange(tasks, events, user, weekStart, weekEnd);
    }

    public Schedule generateSemesterSchedule(List<Task> tasks, List<Event> events, User user, LocalDate semStart,
            LocalDate semEnd) {
        if (semStart == null || semEnd == null || semStart.isAfter(semEnd)) {
            throw new IllegalArgumentException("Invalid semester range");
        }
        return generateScheduleForRange(tasks, events, user, semStart, semEnd);
    }

    @Override
    public Schedule hillClimb(Schedule schedule) {
        Schedule current = schedule;
        double currentScore = score(current);

        int iter = 0;
        while (iter < maxIterations) {
            Schedule neighbor = generateNeighbor(current);
            double neighborScore = score(neighbor);
            if (neighborScore > currentScore) {
                current = neighbor;
                currentScore = neighborScore;
            }
            iter++;
        }
        return current;
    }

    private Schedule generateNeighbor(Schedule schedule) {
        Schedule copy = schedule.deepCopy();

        List<ScheduleEntry> taskEntries = new ArrayList<ScheduleEntry>();
        int i = 0;
        while (i < copy.getEntries().size()) {
            ScheduleEntry e = copy.getEntries().get(i);
            if (e.getTask() != null)
                taskEntries.add(e);
            i++;
        }

        if (taskEntries.size() < 2)
            return copy;

        int a = rand.nextInt(taskEntries.size());
        int b = rand.nextInt(taskEntries.size());
        while (a == b)
            b = rand.nextInt(taskEntries.size());

        ScheduleEntry A = taskEntries.get(a);
        ScheduleEntry B = taskEntries.get(b);

        TimeSlot tmp = A.getSlot();
        A.setSlot(B.getSlot());
        B.setSlot(tmp);

        return copy;
    }

    @Override
    public double score(Schedule schedule) {
        double score = 0.0;

        List<ScheduleEntry> all = schedule.getEntries();
        int i = 0;
        while (i < all.size()) {
            ScheduleEntry e = all.get(i);
            if (e.getTask() != null) {

                score += 5.0;
                score += (e.getTask().getPriority().ordinal() + 1) * 5.0;

                List<TimeSlot> avail = e.getTask().getAvailableSlots();
                if (avail != null && !avail.isEmpty()) {
                    int j = 0;
                    while (j < avail.size()) {
                        TimeSlot a = avail.get(j);
                        if (a.contains(e.getSlot())) {
                            score += 8.0;
                            break;
                        }
                        j++;
                    }
                }

                if (e.getTask().getDeadline() != null) {
                    if (!e.getSlot().getEnd().isAfter(e.getTask().getDeadline())) {
                        score += 3.0;
                    } else {
                        score -= 5.0;
                    }
                }
            }
            i++;
        }

        int n = all.size();
        int x = 0;
        while (x < n) {
            int y = x + 1;
            while (y < n) {
                if (all.get(x).conflictsWith(all.get(y))) {
                    score -= 100.0;
                }
                y++;
            }
            x++;
        }

        return score;
    }

    private LocalDate findMaxDeadline(List<Task> tasks) {
        LocalDate max = LocalDate.now();
        if (tasks == null || tasks.isEmpty()) {
            return max.plusDays(6);
        }
        int i = 0;
        while (i < tasks.size()) {
            Task t = tasks.get(i);
            if (t.getDeadline() != null) {
                LocalDate d = t.getDeadline().toLocalDate();
                if (d.isAfter(max))
                    max = d;
            }
            i++;
        }
        if (!max.isAfter(LocalDate.now())) {
            max = LocalDate.now().plusDays(6);
        }
        return max;
    }
}

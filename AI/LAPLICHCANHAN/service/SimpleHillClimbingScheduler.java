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
        // tìm deadline lớn nhất để làm mốc kết thúc
        LocalDate end = findMaxDeadline(tasks);
        // nếu deadline không hợp lệ thì mặc định 1 tuần
        if (!end.isAfter(start)) {
            end = start.plusDays(6);
        }
        return generateScheduleForRange(tasks, events, user, start, end);
    }

    // tạo lịch trong khoảng start đến khi end
    public Schedule generateScheduleForRange(List<Task> tasks,
            List<Event> events,
            User user,
            LocalDate start,
            LocalDate end) {

        if (start == null || end == null || start.isAfter(end)) {
            throw new IllegalArgumentException("Invalid date range");
        }

        Schedule schedule = new Schedule(user.getId());

        // thêm EVENT trước vì chúng chiếm chỗ cố định
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

        // copy danh sách task + sắp xếp theo:
        // - priority giảm dần
        // - deadline tăng dần
        List<Task> taskList = new ArrayList<Task>();
        if (tasks != null) {
            int ti = 0;
            while (ti < tasks.size()) {
                taskList.add(tasks.get(ti));
                ti++;
            }
        }

        Collections.sort(taskList, new Comparator<Task>() {
            @Override
            public int compare(Task a, Task b) {
                // so sánh priority trước
                int p = b.getPriority().ordinal() - a.getPriority().ordinal();
                if (p != 0)
                    return p;

                // priority bằng cách so deadline (null = thấp hơn)
                if (a.getDeadline() == null)
                    return 1;
                if (b.getDeadline() == null)
                    return -1;
                return a.getDeadline().compareTo(b.getDeadline());
            }
        });

        // lấy những khoảng trống còn lại sau khi trừ event (theo giờ làm việc của user)
        List<TimeSlot> freeSlots = schedule.getFreeSlots(start, end, user.getWorkingHours());

        // bắt đầu đặt từng task vào các freeSlot
        int ti = 0;
        while (ti < taskList.size()) {
            Task t = taskList.get(ti);
            boolean placed = false;

            // ưu tiên đặt trong availableSlots của task
            List<TimeSlot> tAvail = t.getAvailableSlots();
            if (tAvail != null && !tAvail.isEmpty()) {

                int ai = 0;
                while (ai < tAvail.size() && !placed) {
                    TimeSlot avail = tAvail.get(ai);

                    int fi = 0;
                    while (fi < freeSlots.size() && !placed) {
                        TimeSlot free = freeSlots.get(fi);

                        // chọn thời điểm bắt đầu hợp lệ
                        LocalDateTime candStart = free.getStart().isAfter(avail.getStart())
                                ? free.getStart()
                                : avail.getStart();
                        LocalDateTime candEnd = candStart.plusMinutes(t.getDurationMinutes());

                        // nếu khoảng bắt đầu và kết thúc nằm gọn trong cả avail và free
                        if ((!candEnd.isAfter(free.getEnd())) && (!candEnd.isAfter(avail.getEnd()))) {

                            // tạo entry cho task
                            TimeSlot used = new TimeSlot(candStart, candEnd);
                            schedule.addEntry(new ScheduleEntry(EntryType.TASK, used, t, null));

                            // cập nhật freeSlots (tách phần còn lại)
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

            // nếu chưa đặt được thì thử đặt vào freeSlot bất kỳ
            if (!placed) {
                int fi = 0;
                while (fi < freeSlots.size() && !placed) {
                    TimeSlot free = freeSlots.get(fi);

                    // xem có đủ thời gian hay không
                    if (free.lengthMinutes() >= t.getDurationMinutes()) {
                        LocalDateTime candStart = free.getStart();
                        LocalDateTime candEnd = candStart.plusMinutes(t.getDurationMinutes());

                        // nếu có deadline thì không được vượt
                        if (t.getDeadline() != null && candEnd.isAfter(t.getDeadline())) {
                            fi++;
                            continue;
                        }

                        // đặt task
                        TimeSlot used = new TimeSlot(candStart, candEnd);
                        schedule.addEntry(new ScheduleEntry(EntryType.TASK, used, t, null));

                        // tách freeSlot
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

            // đặt đại vào freeSlot mà không cần kiểm deadline neus không có đủ khoảng trống
            // phù hợp để chèn lịch đẹp
            if (!placed && t.getDeadline() != null) {
                int fi = 0;
                while (fi < freeSlots.size() && !placed) {
                    TimeSlot free = freeSlots.get(fi);
                    if (free.lengthMinutes() >= t.getDurationMinutes()) {

                        TimeSlot used = new TimeSlot(
                                free.getStart(),
                                free.getStart().plusMinutes(t.getDurationMinutes()));

                        schedule.addEntry(new ScheduleEntry(EntryType.TASK, used, t, null));
                        // Sau khi đặt Task thì cập nhật lại freeSlot
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

    // tạo lịch cho 1 tuần
    public Schedule generateWeeklySchedule(List<Task> tasks, List<Event> events, User user, LocalDate weekStart) {
        if (weekStart == null)
            throw new IllegalArgumentException("weekStart null");
        return generateScheduleForRange(tasks, events, user, weekStart, weekStart.plusDays(6));
    }

    // tạo lịch cho học kỳ
    public Schedule generateSemesterSchedule(List<Task> tasks, List<Event> events, User user,
            LocalDate semStart, LocalDate semEnd) {
        if (semStart == null || semEnd == null || semStart.isAfter(semEnd))
            throw new IllegalArgumentException("Invalid semester range");
        return generateScheduleForRange(tasks, events, user, semStart, semEnd);
    }

    // thuật toán Hill-Climbing
    @Override
    public Schedule hillClimb(Schedule schedule) {
        Schedule current = schedule;
        double currentScore = score(current);

        int iter = 0;
        while (iter < maxIterations) {
            Schedule neighbor = generateNeighbor(current); // tạo lịch lân cận
            double neighborScore = score(neighbor);

            // chấp nhận lịch tốt hơn
            if (neighborScore > currentScore) {
                current = neighbor;
                currentScore = neighborScore;
            }
            iter++;
        }
        return current;
    }

    // tạo lịch lân cận bằng cách swap 2 task
    private Schedule generateNeighbor(Schedule schedule) {
        Schedule copy = schedule.deepCopy();

        // lấy danh sách entry có chứa task
        List<ScheduleEntry> taskEntries = new ArrayList<>();
        int i = 0;
        while (i < copy.getEntries().size()) {
            ScheduleEntry e = copy.getEntries().get(i);
            if (e.getTask() != null)
                taskEntries.add(e);
            i++;
        }

        if (taskEntries.size() < 2)
            return copy;

        // chọn ngẫu nhiên hai task để hoán đổi timeslot
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

    // tính điểm của lịch
    @Override
    public double score(Schedule schedule) {
        double score = 0.0;

        List<ScheduleEntry> all = schedule.getEntries();
        int i = 0;
        while (i < all.size()) {
            ScheduleEntry e = all.get(i);

            if (e.getTask() != null) {

                score += 5.0; // điểm cơ bản

                // ưu tiên cao thì cộng nhiều điểm
                score += (e.getTask().getPriority().ordinal() + 1) * 5.0;

                // đúng trong availableSlot thì thưởng
                List<TimeSlot> avail = e.getTask().getAvailableSlots();
                if (avail != null && !avail.isEmpty()) {
                    int j = 0;
                    while (j < avail.size()) {
                        if (avail.get(j).contains(e.getSlot())) {
                            score += 8.0;
                            break;
                        }
                        j++;
                    }
                }

                // hoàn thành trước deadline thì cộng, ngược lại trừ
                if (e.getTask().getDeadline() != null) {
                    if (!e.getSlot().getEnd().isAfter(e.getTask().getDeadline()))
                        score += 3.0;
                    else
                        score -= 5.0;
                }
            }
            i++;
        }

        // phạt nặng nếu có xung đột thời gian
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

    // tìm deadline lớn nhất trong danh sách task
    private LocalDate findMaxDeadline(List<Task> tasks) {
        LocalDate max = LocalDate.now();

        if (tasks == null || tasks.isEmpty())
            return max.plusDays(6);

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

        // nếu không có deadline nào hợp lệ -> mặc định 1 tuần
        if (!max.isAfter(LocalDate.now())) {
            max = LocalDate.now().plusDays(6);
        }
        return max;
    }
}

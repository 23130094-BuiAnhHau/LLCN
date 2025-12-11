package LAPLICHCANHAN.test;

import LAPLICHCANHAN.model.*;
import LAPLICHCANHAN.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SchedulerConflictTest {

    public static void main(String[] args) {

        // ------------------------
        // Chuẩn bị dữ liệu mẫu
        // ------------------------
        User user = new User("Hau", "hau@example.com");

        user.addWorkingHour(new TimeSlot(
                LocalDateTime.now().withHour(6).withMinute(0).withSecond(0).withNano(0),
                LocalDateTime.now().withHour(22).withMinute(0).withSecond(0).withNano(0)));

        List<Event> events = new ArrayList<Event>();

        events.add(new Event(
                "Lecture: Networks",
                LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0),
                LocalDateTime.now().plusDays(1).withHour(12).withMinute(0).withSecond(0).withNano(0),
                true));

        events.add(new Event(
                "Team Meeting",
                LocalDateTime.now().plusDays(3).withHour(14).withMinute(0).withSecond(0).withNano(0),
                LocalDateTime.now().plusDays(3).withHour(15).withMinute(0).withSecond(0).withNano(0),
                true));

        List<Task> tasks = new ArrayList<Task>();

        Task onThi = new Task("Ôn thi lập trình mạng", 180, Priority.HIGH);
        onThi.setDeadline(LocalDateTime.of(2025, 12, 22, 23, 0));
        tasks.add(onThi);

        Task readPaper = new Task("Đọc bài báo", 90, Priority.MEDIUM);
        tasks.add(readPaper);

        Task project = new Task("Làm project", 240, Priority.HIGH);

        List<TimeSlot> avail = project.getAvailableSlots();

        avail.add(new TimeSlot(
                LocalDateTime.now().plusDays(2).withHour(18).withMinute(0).withSecond(0).withNano(0),
                LocalDateTime.now().plusDays(2).withHour(22).withMinute(0).withSecond(0).withNano(0)));
        tasks.add(project);

        SimpleHillClimbingScheduler scheduler = new SimpleHillClimbingScheduler();

        System.out.println("=== 1) Default generateInitialSchedule (day-range default) ===");
        Schedule initial = scheduler.generateInitialSchedule(tasks, events, user);
        printScheduleEntries(initial);

        // 2) Weekly schedule: bắt đầu từ tuần kế tiếp (weekStart)
        LocalDate weekStart = LocalDate.now().plusDays(1); // tuần bắt đầu từ ngày mai
        System.out.println("\n=== 2) Weekly schedule from " + weekStart + " ===");
        Schedule week = scheduler.generateWeeklySchedule(tasks, events, user, weekStart);
        printScheduleEntries(week);

        LocalDate monthStart = LocalDate.now();
        LocalDate monthEnd = monthStart.plusDays(29);
        System.out.println("\n=== 3) 30-day schedule (month-like) from " + monthStart + " to " + monthEnd + " ===");
        Schedule month = scheduler.generateScheduleForRange(tasks, events, user, monthStart, monthEnd);
        printScheduleEntries(month);

        LocalDate semStart = LocalDate.of(2025, 9, 1);
        LocalDate semEnd = LocalDate.of(2025, 12, 31);
        System.out.println("\n=== 4) Semester schedule from " + semStart + " to " + semEnd + " ===");
        Schedule sem = scheduler.generateSemesterSchedule(tasks, events, user, semStart, semEnd);
        printScheduleEntries(sem);

        System.out.println("\n=== 5) Hill Climbing optimize weekly schedule ===");
        Schedule optimizedWeek = scheduler.hillClimb(week);
        printScheduleEntries(optimizedWeek);
        System.out.println("Final score: " + scheduler.score(optimizedWeek));
    }

    private static void printScheduleEntries(Schedule sch) {
        if (sch == null) {
            System.out.println("(null schedule)");
            return;
        }
        List<ScheduleEntry> entries = sch.getEntries();
        if (entries == null || entries.isEmpty()) {
            System.out.println("(no entries)");
            return;
        }
        for (ScheduleEntry e : entries) {
            System.out.println(e);
        }
    }
}

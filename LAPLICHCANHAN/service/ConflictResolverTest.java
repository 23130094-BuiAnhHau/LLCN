package LAPLICHCANHAN.service;

import LAPLICHCANHAN.model.*;
import LAPLICHCANHAN.service.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ConflictResolverTest {
	 public static void main(String[] args) {

	        UUID testUserId = UUID.randomUUID(); 
	        Schedule mySchedule = new Schedule(testUserId);
	        
	        // 1. Tạo đối tượng Event và TimeSlot tương ứng (10:00 - 11:00)
	     
	        		LocalDateTime startTime = LocalDateTime.of(2025, 1, 1, 10, 0);
	        		LocalDateTime endTime = LocalDateTime.of(2025, 1, 1, 11, 0);
	       
			Event eventObj = new Event("Hop Nhom", startTime, endTime, true); // fixed=true
	        // Bọc Event vào trong ScheduleEntry, TRUYỀN VÀO slotA
			TimeSlot slotA = new TimeSlot(
		            LocalDateTime.of(2025, 12, 15, 10, 30), 
		            LocalDateTime.of(2025, 12, 15, 11, 30)
		        );
	        ScheduleEntry entryA = new ScheduleEntry(ScheduleEntry.EntryType.EVENT, slotA, null, eventObj);
	                                        
	        // 2. Tạo đối tượng Task và TimeSlot tương ứng (10:30 - 11:30, trùng giờ)
	        Task taskObj = new Task(
	            UUID.randomUUID(),
	            "Viet bao cao", 
	            60, // Giả sử durationMinutes
	            LocalDateTime.of(2025, 12, 15, 11, 30), // Giả sử deadline 
	            Priority.MEDIUM // Giả sử enum Priority tồn tại
	        );
	        // PHẢI TẠO TIMESLOT TRƯỚC
	        TimeSlot slotB = new TimeSlot(
	            LocalDateTime.of(2025, 12, 15, 10, 30), 
	            LocalDateTime.of(2025, 12, 15, 11, 30)
	        );
	        // Bọc Task vào trong ScheduleEntry, TRUYỀN VÀO slotB
	        ScheduleEntry entryB = new ScheduleEntry(ScheduleEntry.EntryType.TASK, slotB, taskObj, null);

	        mySchedule.addEntry(entryA);
	        mySchedule.addEntry(entryB);

	        System.out.println("--- Chay thu kiem tra xung dot ---");
	        // Sử dụng getSlot().getStart() như trong class ScheduleEntry của bạn
	        System.out.println("Entry A (Event): " + entryA.toString());
	        System.out.println("Entry B (Task):  " + entryB.toString());


	        // --- Chạy bộ giải quyết xung đột ---
	        ConflictResolver resolver = new ConflictResolverImpl();
	        List<ConflictResolver.Conflict> conflicts = resolver.detectConflicts(mySchedule);

	        System.out.println("\n--- Ket qua phat hien ---");
	        if (conflicts.isEmpty()) {
	            System.out.println("=> KHONG PHAT HIEN XUNG DOT.");
	        } else {
	            System.out.println("=> PHAT HIEN XUNG DOT THANH CONG!"); 
	            System.out.println("So luong xung dot: " + conflicts.size());
	            System.out.println("Chi tiet xung dot dau tien: " + conflicts.get(0));
	        }
	    }
}

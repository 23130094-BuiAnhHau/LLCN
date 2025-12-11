package LAPLICHCANHAN.service;

import java.util.ArrayList;
import java.util.List;

import LAPLICHCANHAN.model.Schedule;
import LAPLICHCANHAN.model.ScheduleEntry;

public class ConflictResolverImpl implements ConflictResolver{


	@Override
	public List<Conflict> detectConflicts(Schedule schedule) {
		List<Conflict> conflicts = new ArrayList<>();
		List<ScheduleEntry> entries = schedule.getEntries();
		
		for (int i = 0; i < entries.size(); i++) {
			for (int j = i + 1; j < entries.size(); j++) {
				ScheduleEntry entryA = entries.get(i);
				ScheduleEntry entryB = entries.get(j);
				
				 if (entryA.conflictsWith(entryB)) { 
	                    conflicts.add(new Conflict(entryA, entryB));
	                }	
			}
		}
		return conflicts;
	}

	@Override
	public List<ResolutionOption> suggestResolutions(Conflict conflict) {
		List<ResolutionOption> options = new ArrayList<>();
		options.add(new ResolutionOption(
	            "Sử dụng chiến lược tự động (Ưu tiên EVENT hơn TASK)",
	            "AUTO_RESOLVE" // Mã hành động đơn giản
	        ));
	        
	        // Thêm lựa chọn thủ công rõ ràng hơn:
	        options.add(new ResolutionOption(
	            "Giữ Event/Task A (" + conflict.a.getTitle() + ")",
	            "KEEP_A" 
	        ));

	         options.add(new ResolutionOption(
	            "Giữ Event/Task B (" + conflict.b.getTitle() + ")",
	            "KEEP_B" 
	        ));
	        
	        return options;
	}

	@Override
	public ResolutionResult autoResolve(Conflict conflict) {
	    ScheduleEntry entryA = conflict.a;
	    ScheduleEntry entryB = conflict.b;

	    // Sử dụng Enum EntryType của bạn để so sánh
	    boolean isAEvent = entryA.getType() == ScheduleEntry.EntryType.EVENT;
	    boolean isBEvent = entryB.getType() == ScheduleEntry.EntryType.EVENT;

	    if (isAEvent && !isBEvent) {
	        // Giữ A (Event), cần loại bỏ B (Task) khỏi lịch chính.
	        // TRUYỀN THAM SỐ ĐÚNG VÀO CONSTRUCTOR
	        return new ResolutionResult(
	            true, 
	            "Auto: Giữ Event \"" + entryA.getTitle() + "\", đề xuất dời Task \"" + entryB.getTitle() + "\"."
	        );
	    }
	    else if (!isAEvent && isBEvent) {
	        // Giữ B (Event), cần loại bỏ A (Task) khỏi lịch chính.
	        // TRUYỀN THAM SỐ ĐÚNG VÀO CONSTRUCTOR
	        return new ResolutionResult(
	            true, 
	            "Auto: Giữ Event \"" + entryB.getTitle() + "\", đề xuất dời Task \"" + entryA.getTitle() + "\"."
	        );
	    }
	    else {
	        // Cả hai đều là Event hoặc cả hai đều là Task -> cần can thiệp thủ công
	        // TRUYỀN THAM SỐ ĐÚNG VÀO CONSTRUCTOR
	        return new ResolutionResult(
	            false, 
	            "Không thể tự động giải quyết (cùng loại Event/Task, hoặc cần ưu tiên khác)."
	        );
	    }
	}


}

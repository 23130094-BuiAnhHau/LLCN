package LAPLICHCANHAN.service;

import LAPLICHCANHAN.model.*;

import java.util.List;

public interface ConflictResolver {

    List<Conflict> detectConflicts(Schedule schedule);

    List<ResolutionOption> suggestResolutions(Conflict conflict);

    ResolutionResult autoResolve(Conflict conflict);

    /**
     * Lớp đại diện một xung đột giữa hai mục trong lịch.
     * Ví dụ: Task A trùng giờ với Event B.
     */
    class Conflict {
        public ScheduleEntry a; // Mục thứ nhất bị xung đột
        public ScheduleEntry b; // Mục thứ hai bị xung đột

        public Conflict(ScheduleEntry a, ScheduleEntry b) {
            this.a = a;
            this.b = b;
        }
    }

    class ResolutionOption {
        public String description; // Mô tả phương án
        public Runnable apply; // Hành động thực thi nếu chọn phương án này
    }

    /**
     * Kết quả sau khi cố gắng xử lý xung đột.
     *
     * success: true nếu xử lý thành công
     * message: thông báo mô tả kết quả
     */
    class ResolutionResult {
        public boolean success; // Thành công hay thất bại
        public String message; // Ghi chú chi tiết
    }
}

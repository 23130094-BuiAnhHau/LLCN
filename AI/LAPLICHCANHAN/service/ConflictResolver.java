package LAPLICHCANHAN.service;

import LAPLICHCANHAN.model.*;

import java.util.List;

/**
 * ConflictResolver: Interface chịu trách nhiệm:
 * - Phát hiện xung đột trong lịch (Conflict Detection)
 * - Đề xuất phương án giải quyết (Resolution Options)
 * - Tự động xử lý xung đột nếu có thể (Auto Resolve)
 *
 * Xung đột có thể xảy ra khi:
 * - Hai tác vụ (Task) hoặc sự kiện (Event) trùng thời gian
 * - Task bị chồng chéo nhiều Event cố định
 * - Người dùng có hai hoạt động cùng giờ
 */
public interface ConflictResolver {

    /**
     * Phát hiện tất cả các xung đột trong một lịch.
     *
     * @param schedule Lịch cần kiểm tra
     * @return Danh sách các xung đột, mỗi xung đột gồm 2 ScheduleEntry chồng chéo
     *         nhau
     */
    List<Conflict> detectConflicts(Schedule schedule);

    /**
     * Gợi ý các cách để giải quyết xung đột.
     *
     * Ví dụ Resolution Option:
     * - Dời Task A sang thời gian khác
     * - Ưu tiên Event B -> di chuyển Task A
     * - Rút ngắn thời lượng Task/Event
     *
     * @param conflict Xung đột cụ thể cần giải quyết
     * @return Danh sách phương án xử lý xung đột
     */
    List<ResolutionOption> suggestResolutions(Conflict conflict);

    /**
     * Tự động xử lý một xung đột theo chiến lược mặc định.
     *
     * Chiến lược có thể là:
     * - Ưu tiên Event hơn Task
     * - Ưu tiên Task quan trọng hơn
     * - Tối thiểu hóa thay đổi trong lịch
     *
     * @param conflict Xung đột cần xử lý tự động
     * @return Kết quả xử lý (thành công hoặc thất bại kèm thông báo)
     */
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

    /**
     * Lớp mô tả một lựa chọn để giải quyết xung đột.
     *
     * description: mô tả cách xử lý
     * apply: hành động cụ thể (có thể dùng Command Pattern)
     */
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

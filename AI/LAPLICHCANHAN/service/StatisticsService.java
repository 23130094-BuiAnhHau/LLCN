package LAPLICHCANHAN.service;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * StatisticsService:
 * Interface cung cấp các chức năng thống kê cho người dùng,
 * giúp phân tích mức độ hoàn thành công việc và phân bố khối lượng công việc.
 *
 * Các thống kê này có thể được dùng để:
 * - Đánh giá hiệu suất làm việc theo ngày/tuần/tháng
 * - Phân tích người dùng đang dành nhiều thời gian cho loại công việc nào
 * - Gợi ý cách tối ưu lịch làm việc
 */
public interface StatisticsService {

    /**
     * Tính tỷ lệ hoàn thành công việc của người dùng trong khoảng thời gian.
     *
     * @param userId ID người dùng cần thống kê
     * @param from   Ngày bắt đầu
     * @param to     Ngày kết thúc
     * @return Tỷ lệ hoàn thành (0.0 → 1.0 hoặc dạng phần trăm)
     *
     *         Cách tính phổ biến:
     *         completionRate = số_task_đã_hoàn_thành / tổng_số_task
     *
     *         Ví dụ:
     *         - Có 10 task, hoàn thành 7 -> completionRate = 0.7
     */
    double completionRate(UUID userId, LocalDate from, LocalDate to);

    /**
     * Thống kê phân bố khối lượng công việc theo từng loại.
     *
     * @param userId ID của người dùng
     * @param from   Ngày bắt đầu
     * @param to     Ngày kết thúc
     * @return Map dạng:
     *         key -> tên loại công việc (ví dụ: "Work", "Study", "Health")
     *         value -> số lượng hoặc tổng thời gian dành cho mỗi loại
     *
     *         Ví dụ:
     *         {
     *         "Work": 12,
     *         "Study": 5,
     *         "Exercise": 3
     *         }
     *
     *         Ý nghĩa:
     *         - Giúp xem người dùng đang tập trung vào mảng nào nhiều nhất
     *         - Hỗ trợ thuật toán tối ưu lịch cân bằng workload
     */
    Map<String, Integer> workloadDistribution(UUID userId, LocalDate from, LocalDate to);
}

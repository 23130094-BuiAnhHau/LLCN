package LAPLICHCANHAN.service;

import LAPLICHCANHAN.model.*;

import java.util.List;

/**
 * Scheduler: Interface định nghĩa các chức năng để tạo và tối ưu lịch làm việc.
 * 
 * Bao gồm:
 * - Tạo lịch ban đầu (dựa trên greedy hoặc heuristic)
 * - Cải thiện lịch bằng thuật toán leo đồi (hill climbing)
 * - Chấm điểm lịch để đánh giá chất lượng
 * - Pipeline tối ưu hoàn chỉnh
 */
public interface Scheduler {

    /**
     * Tạo lịch ban đầu theo phương pháp greedy hoặc heuristic.
     * 
     * @param tasks  Danh sách các Task cần sắp xếp
     * @param events Danh sách Event cố định (cuộc họp, lịch bận,...)
     * @param user   Người dùng mà lịch sẽ được tạo cho
     * @return Lịch ban đầu đã được tạo
     */
    Schedule generateInitialSchedule(List<Task> tasks, List<Event> events, User user);

    /**
     * Áp dụng thuật toán Hill Climbing để cải thiện lịch:
     * - Giảm thời gian trống (idle time)
     * - Giảm xung đột thời gian
     * - Tăng điểm chất lượng tổng thể của lịch
     * 
     * @param schedule Lịch ban đầu
     * @return Lịch sau khi đã được tối ưu qua hill climbing
     */
    Schedule hillClimb(Schedule schedule);

    /**
     * Chấm điểm chất lượng của một lịch.
     * Điểm càng cao thì lịch càng tối ưu.
     *
     * Ví dụ tiêu chí chấm điểm:
     * - Ít xung đột
     * - Ít idle time
     * - Ưu tiên các task theo mức độ quan trọng
     * 
     * @param schedule Lịch cần đánh giá
     * @return Điểm số từ 0 -> 1 hoặc thang bất kỳ (càng lớn càng tốt)
     */
    double score(Schedule schedule);

    /**
     * Pipeline tối ưu đầy đủ:
     * 1. Tạo lịch ban đầu
     * 2. Tối ưu bằng hill climbing
     *
     * @param tasks  Danh sách Task cần sắp xếp
     * @param events Danh sách Event cố định
     * @param user   Người dùng
     * @return Lịch tối ưu cuối cùng
     */
    default Schedule optimize(List<Task> tasks, List<Event> events, User user) {
        Schedule s = generateInitialSchedule(tasks, events, user);
        return hillClimb(s);
    }
}

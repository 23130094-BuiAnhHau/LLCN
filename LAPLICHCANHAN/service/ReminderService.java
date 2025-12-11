package LAPLICHCANHAN.service;

import LAPLICHCANHAN.model.Notification;
import LAPLICHCANHAN.model.ScheduleEntry;

/**
 * ReminderService:
 *  Interface xử lý chức năng nhắc nhở cho người dùng.
 *
 *  Bao gồm:
 *   - Lên lịch nhắc nhở trước một khoảng thời gian (ví dụ: nhắc trước 10 phút)
 *   - Gửi thông báo nhắc nhở khi đến thời điểm
 *
 *  Hệ thống này thường được sử dụng để nhắc:
 *   - Task sắp đến thời gian thực hiện
 *   - Event quan trọng như cuộc họp, buổi hẹn
 */
public interface ReminderService {

    /**
     * Tạo một nhắc nhở cho một mục trong lịch (ScheduleEntry).
     *
     * @param entry         Mục trong lịch (Task/Event) cần được nhắc
     * @param aheadMinutes  Thời gian nhắc trước (tính bằng phút)
     *
     * Ví dụ:
     *   scheduleReminder(entry, 15)
     *   → Hệ thống sẽ nhắc trước 15 phút khi entry sắp diễn ra.
     */
    void scheduleReminder(ScheduleEntry entry, int aheadMinutes);

    /**
     * Gửi một thông báo nhắc nhở đến người dùng.
     *
     * @param notification  Thông báo chứa nội dung, thời gian, người nhận...
     *
     * Cách triển khai có thể là:
     *  - Gửi email
     *  - Gửi push notification
     *  - Gửi SMS
     *  - Hiển thị popup trong ứng dụng
     */
    void sendReminder(Notification notification);
}

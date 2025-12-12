package LAPLICHCANHAN.service;

import LAPLICHCANHAN.model.Notification;
import LAPLICHCANHAN.model.ScheduleEntry;

/**
 * ReminderService:
 * Interface xử lý chức năng nhắc nhở cho người dùng.
 * 
 */
public interface ReminderService {

    void scheduleReminder(ScheduleEntry entry, int aheadMinutes);

    void sendReminder(Notification notification);
}

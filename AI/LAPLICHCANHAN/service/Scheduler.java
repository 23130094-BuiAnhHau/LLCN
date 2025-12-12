package LAPLICHCANHAN.service;

import LAPLICHCANHAN.model.*;

import java.util.List;

/**
 * Scheduler: Interface định nghĩa các chức năng để tạo và tối ưu lịch làm việc.
 */
public interface Scheduler {

    Schedule generateInitialSchedule(List<Task> tasks, List<Event> events, User user);

    Schedule hillClimb(Schedule schedule);

    double score(Schedule schedule);

    default Schedule optimize(List<Task> tasks, List<Event> events, User user) {
        Schedule s = generateInitialSchedule(tasks, events, user);
        return hillClimb(s);
    }
}

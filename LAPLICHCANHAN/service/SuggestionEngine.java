package LAPLICHCANHAN.service;

import LAPLICHCANHAN.model.*;

import java.util.List;

public interface SuggestionEngine {
    /**
     * Trả về list slot xếp theo rank tốt nhất cho task (dựa deadline, priority, free time).
     */
    List<TimeSlot> suggestBestSlotsForTask(Task task, Schedule schedule, User user, int topK);
}




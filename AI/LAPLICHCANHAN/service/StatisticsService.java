package LAPLICHCANHAN.service;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

//Thống kê
public interface StatisticsService {

    double completionRate(UUID userId, LocalDate from, LocalDate to);

    Map<String, Integer> workloadDistribution(UUID userId, LocalDate from, LocalDate to);
}
